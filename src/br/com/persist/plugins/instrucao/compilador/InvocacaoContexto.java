package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.processador.Biblioteca;
import br.com.persist.plugins.instrucao.processador.Funcao;

public class InvocacaoContexto extends Container {
	public static final String INVOKE_PARAM_EXP = "invoke_param_exp";
	public static final String INVOKE_PARAM = "invoke_param";
	public static final String INVOKE_EXP = "invoke_exp";
	public static final String INVOKE = "invoke";

	public InvocacaoContexto(Token token) {
		adicionar(new ArgumentoContexto(null));
		contexto = Contextos.ABRE_PARENTESES;
		this.token = token;
	}

	public ArgumentoContexto getArgumento() {
		return (ArgumentoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(getArgumento());
		contexto = Contextos.PONTO_VIRGULA;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void indexar(Indexador indexador) {
		pontoDeslocamento = indexador.value();
		super.indexar(indexador);
		sequencia = indexador.get3();
		indexarNegativo(indexador);
	}

	@Override
	protected void declInvocDiverImpl() throws InstrucaoException {
		if (ehInvokeParam()) {
			return;
		}
		BibliotecaContexto biblio = getBiblioteca();
		String id = token.string;
		if (biblio == null) {
			throw new InstrucaoException("erro.funcao_parent", id);
		}
		String[] strings = id.split("\\.");
		if (strings.length == 1) {
			Container funcao = biblio.getFuncao(id);
			validarFuncaoContextoDivergencia(funcao, id, getArgumento());
		} else {
			try {
				Biblioteca biblioteca = biblio.cacheBiblioteca.getBiblioteca(strings[0]);
				Funcao funcao = biblioteca.getFuncao(strings[1]);
				validarFuncaoInstrucaoDivergencia(funcao, getArgumento());
			} catch (InstrucaoException ex) {
				throw new InstrucaoException(ex.getMessage(), false);
			}
		}
	}

	static void validarFuncaoContextoDivergencia(Container funcao, String identity, ArgumentoContexto argumento)
			throws InstrucaoException {
		if (funcao == null) {
			throw new InstrucaoException("Funcao inexistente >>> " + identity, false);
		}
		ParametrosContexto parametros = null;
		if (funcao instanceof FuncaoContexto) {
			parametros = ((FuncaoContexto) funcao).getParametros();
		} else if (funcao instanceof FuncaoNativaContexto) {
			parametros = ((FuncaoNativaContexto) funcao).getParametros();
		}
		if (parametros == null) {
			throw new InstrucaoException("Parametros inexistente >>> " + identity, false);
		}
		if (parametros.getSize() != argumento.getSize()) {
			throw new InstrucaoException("erro.divergencia_qtd_decl_invocacao", identity);
		}
	}

	static void validarFuncaoInstrucaoDivergencia(Funcao funcao, ArgumentoContexto argumento)
			throws InstrucaoException {
		if (funcao.getTotalParametro() != argumento.getSize()) {
			throw new InstrucaoException("erro.divergencia_qtd_decl_invocacao", funcao.getNome());
		}
	}

	@Override
	public void salvar(PrintWriter pw) throws InstrucaoException {
		super.salvar(pw);
		if (ehInvokeParam()) {
			print(pw, INVOKE_PARAM, token.string);
		} else {
			print(pw, INVOKE, token.string);
		}
		salvarNegativo(pw);
	}

	private boolean ehInvokeParam() throws InstrucaoException {
		FuncaoContexto funcao = getFuncao();
		if (funcao == null) {
			throw new InstrucaoException("erro.funcao_parent", token.string);
		}
		ParametrosContexto parametros = funcao.getParametros();
		return parametros.contem(token.string);
	}

	@Override
	public String toString() {
		return "invocacao >>> " + getArgumento().toString();
	}
}