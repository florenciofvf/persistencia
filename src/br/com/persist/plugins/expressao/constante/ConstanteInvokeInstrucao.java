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

/**
 * <pre>
*defun fatorial(numero) {
*	const exec = defun calcular(valor) {
*					if(valor == 0) {
*						return 1;
*					} else {
*						return valor * calcular(valor - 1);
*					}
*				};
*	return exec(numero);
*}
 * </pre>
 */
public class ConstanteInvokeInstrucao extends Instrucao implements LinkBiblioteca {
	private String nomeBiblioteca;
	private String nomeConstante;
	private boolean biblioLocal;

	public ConstanteInvokeInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, ConstanteContexto.INVOKE_CONST);
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
		Funcao funcaoValor = (Funcao) constante.getValor();
		Funcao clone = funcaoValor.clonar();
		pilhaOperando.setArgumentos(clone);
		if (ExpressaoConstantes.DEBUG) {
			String string = ExpressaoUtil.completar("[INVOKE-CONST-" + nomeBiblioteca + "." + nomeConstante
					+ "] ######### (funcao valor) ######### " + clone);
			ExpressaoUtil.print(string, pilhaOperando);
		}
		pilhaFuncao.push(clone);
	}

	@Override
	public String toString() {
		return super.toString() + " " + nomeBiblioteca + "." + nomeConstante;
	}
}