package br.com.persist.plugins.expressao.funcao;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBiblioteca;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class FuncaoLoadInstrucao extends Instrucao implements LinkBiblioteca {
	private final boolean tipoVoid;
	private String nomeBiblioteca;
	private boolean biblioLocal;
	private String nomeFuncao;

	public FuncaoLoadInstrucao(boolean tipoVoid, int indice, String parametros) throws ExpressaoException {
		super(indice, tipoVoid ? FuncaoContexto.LOAD_FUNCTION_VOID : FuncaoContexto.LOAD_FUNCTION_CRET);
		this.tipoVoid = tipoVoid;
		String[] array = parametros.split(ExpressaoConstantes.ESPACO);
		nomeBiblioteca = array[0];
		nomeFuncao = array[1];
		biblioLocal = Contexto.THIS.equals(nomeBiblioteca);
	}

	@Override
	public String getNomeBiblioAbsoluto() {
		return nomeBiblioteca;
	}

	@Override
	public boolean isRefLocal() {
		return biblioLocal;
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Biblioteca biblio;
		if (biblioLocal) {
			biblio = funcao.getBiblioteca();
		} else {
			biblio = (Biblioteca) pilhaOperando.pop();
		}
		Funcao funcaoLoad = biblio.getFuncao(nomeFuncao);
		if (tipoVoid != funcaoLoad.isTipoVoid()) {
			String chamada = nomeBiblioteca + "." + nomeFuncao;
			throw new ExpressaoException("erro.invocacao.retorno", chamada,
					(funcaoLoad.isTipoVoid() ? "VOID" : "VALOR"));
		}
		pilhaOperando.push(funcaoLoad);
	}
}