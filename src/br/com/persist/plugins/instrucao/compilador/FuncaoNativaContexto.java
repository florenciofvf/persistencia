package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncaoNativaContexto extends Container {
	public static final OperadorOuFinalizar OPERADOR_OU_FINALIZAR = new OperadorOuFinalizar();
	private final FuncaoIdentityContexto identityBiblio;
	private final FuncaoIdentityContexto identity;
	private boolean identityVoid;
	private boolean retornoVoid;
	private boolean faseBiblio;

	public FuncaoNativaContexto() {
		identityBiblio = new FuncaoIdentityContexto();
		identity = new FuncaoIdentityContexto();
		adicionar(new ParametrosContexto());
		contexto = identityBiblio;
		faseBiblio = true;
	}

	public String getNome() {
		return getIdentity().toString();
	}

	public ParametrosContexto getParametros() {
		return (ParametrosContexto) get(0);
	}

	public FuncaoIdentityContexto getIdentityBiblio() {
		return identityBiblio;
	}

	public FuncaoIdentityContexto getIdentity() {
		return identity;
	}

	public boolean isRetornoVoid() {
		return retornoVoid;
	}

	public void setRetornoVoid(boolean retornoVoid) {
		this.retornoVoid = retornoVoid;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(getParametros());
		contexto = OPERADOR_OU_FINALIZAR;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.operador(compilador, token);
		contexto = Contextos.IDENTITY;
		identityVoid = true;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		if (identityVoid) {
			if (!"void".equals(token.getString())) {
				compilador.invalidar(token);
			} else {
				compilador.setContexto(getPai());
				retornoVoid = true;
			}
		} else if (faseBiblio) {
			contexto = identity;
			faseBiblio = false;
		} else {
			contexto = Contextos.ABRE_PARENTESES;
		}
	}

	public void indexar() {
		Indexador indexador = new Indexador();
		indexar(indexador);
	}

	@Override
	public void salvar(PrintWriter pw) {
		pw.println(InstrucaoConstantes.PREFIXO_FUNCAO_NATIVA + Util.replaceAll(identityBiblio.toString(), "_", ".")
				+ " " + identity);
		getParametros().salvar(pw);
	}

	@Override
	public String toString() {
		return "function_native >>> " + getParametros().toString();
	}
}

class OperadorOuFinalizar extends AbstratoContexto {
	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!";".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		if (!":".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}
}