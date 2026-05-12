package br.com.persist.plugins.expressao.constante;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoUtil;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBiblioteca;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ConstanteLoadInstrucao extends Instrucao implements LinkBiblioteca {
	private String nomeBiblioteca;
	private String nomeConstante;
	private boolean biblioLocal;

	public ConstanteLoadInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, ConstanteContexto.LOAD_CONST);
		String[] array = parametros.split(ExpressaoConstantes.ESPACO);
		nomeBiblioteca = array[0];
		nomeConstante = array[1];
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
		Constante constante = biblio.getConstante(nomeConstante);
		pilhaOperando.push(constante.getValor());
		if (ExpressaoConstantes.DEBUG_INSTRUCAO) {
			String string = ExpressaoUtil.completar("[LOAD-CONST-" + nomeBiblioteca + "." + nomeConstante
					+ "] ######### (const load) ######### " + constante);
			ExpressaoUtil.print(string, pilhaOperando);
		}
	}

	@Override
	public String toString() {
		return super.toString() + " " + nomeBiblioteca + "." + nomeConstante;
	}
}