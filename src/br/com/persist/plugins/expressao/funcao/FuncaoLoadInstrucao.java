package br.com.persist.plugins.expressao.funcao;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBiblioteca;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Load;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class FuncaoLoadInstrucao extends FuncaoLoad implements LinkBiblioteca, Load {
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
		Funcao funcaoLoad = biblio.getFuncao(nomeFuncao).clonar();
		checarTipo(tipoVoid, funcaoLoad, nomeBiblioteca, nomeFuncao);
		pilhaOperando.push(funcaoLoad);
		log(get(nomeBiblioteca, nomeFuncao) + "] ######### (funcao load) ######### " + funcaoLoad, pilhaOperando);
	}

	private String get(String nomeBiblioteca, String nomeFuncao) {
		return "[" + (tipoVoid ? FuncaoContexto.LOAD_FUNCTION_VOID.toUpperCase()
				: FuncaoContexto.LOAD_FUNCTION_CRET.toUpperCase()) + "-" + nomeBiblioteca + "." + nomeFuncao;
	}

	@Override
	public String toString() {
		return super.toString() + " " + nomeBiblioteca + "." + nomeFuncao;
	}
}