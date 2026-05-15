package br.com.persist.plugins.expressao.local;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.constante.Constante;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Invoke;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

/**
 * <pre>
*defun fatorial(numero) {
*	local exec = defun calcular(valor) {
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
public class LocalInvokeInstrucao extends Instrucao implements Invoke {
	private String[] nomeFuncoes;
	private String nomeLocal;

	public LocalInvokeInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, LocalContexto.INVOKE_LOCAL);
		int pos = parametros.indexOf(' ');
		nomeFuncoes = parametros.substring(0, pos).split(CIFRAO);
		nomeLocal = parametros.substring(pos + 1);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Funcao funcaoAlvo = getFuncaoAlvo(funcao, nomeFuncoes);
		Constante constante = funcaoAlvo.getConstante(nomeLocal);
		Funcao funcaoValor = (Funcao) constante.getValor();
		Funcao clone = funcaoValor.clonar();
		pilhaOperando.setArgumentos(clone);
		pilhaFuncao.push(clone);
		log("[INVOKE-LOCAL-" + get(nomeFuncoes, nomeLocal) + "] ######### (funcao valor) ######### " + clone,
				pilhaOperando);
	}

	@Override
	public String toString() {
		return super.toString() + " " + get(nomeFuncoes, nomeLocal);
	}
}