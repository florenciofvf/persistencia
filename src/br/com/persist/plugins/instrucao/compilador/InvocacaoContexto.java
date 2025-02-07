package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;
import br.com.persist.plugins.instrucao.processador.Biblioteca;
import br.com.persist.plugins.instrucao.processador.Funcao;
import br.com.persist.plugins.instrucao.processador.Invocacao;

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
	protected void validarImpl() throws InstrucaoException {
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
			validarImpl(funcao, id, getArgumento(), false);
		} else {
			try {
				Biblioteca biblioteca = biblio.cacheBiblioteca.getBiblioteca(strings[0]);
				Funcao funcao = biblioteca.getFuncao(strings[1]);
				validarImpl(funcao, getArgumento(), false);
			} catch (InstrucaoException ex) {
				throw new InstrucaoException(ex.getMessage(), false);
			}
		}
	}

	static void validarImpl(Container funcao, String identity, ArgumentoContexto argumento, boolean exp)
			throws InstrucaoException {
		if (funcao == null) {
			throw new InstrucaoException("Funcao inexistente >>> " + identity, false);
		}
		if (!(funcao instanceof IFuncaoContexto)) {
			throw new InstrucaoException("N\u00E3 \u00E9 fun\u00E7\u00E3o >>> " + identity, false);
		}
		IFuncaoContexto funcaoContexto = (IFuncaoContexto) funcao;
		ParametrosContexto parametros = funcaoContexto.getParametros();
		if (parametros == null) {
			throw new InstrucaoException("Parametros nulo. >>> " + identity, false);
		}
		if (exp && funcaoContexto.isRetornoVoid()) {
			throw new InstrucaoException("erro.funcao_sem_retorno", funcaoContexto.getNome(),
					funcaoContexto.getBiblioteca().getNome());
		} else if (!exp && !funcaoContexto.isRetornoVoid()) {
			throw new InstrucaoException("erro.funcao_com_retorno", funcaoContexto.getNome(),
					funcaoContexto.getBiblioteca().getNome());
		}
		if (parametros.getSize() != argumento.getSize()) {
			throw new InstrucaoException("erro.divergencia_qtd_decl_invocacao", identity, "" + parametros.getSize(),
					"" + argumento.getSize());
		}
	}

	static void validarImpl(Funcao funcao, ArgumentoContexto argumento, boolean exp) throws InstrucaoException {
		Invocacao.validar(funcao, exp, argumento.getSize());
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		super.salvar(compilador, pw);
		if (ehInvokeParam()) {
			compilador.tokens.add(token.novo(Tipo.PARAMETRO));
			print(pw, INVOKE_PARAM, token.string, "" + getArgumento().getSize());
		} else {
			print(pw, INVOKE, token.string);
		}
		salvarNegativo(compilador, pw);
	}

	private boolean ehInvokeParam() throws InstrucaoException {
		IFuncaoContexto funcao = getFuncao();
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