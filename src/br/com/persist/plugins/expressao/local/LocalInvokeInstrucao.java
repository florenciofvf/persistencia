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
	private final boolean comRetorno;
	private String[] nomeFuncoes;
	private String nomeLocal;

	public LocalInvokeInstrucao(boolean comRetorno, int indice, String parametros) throws ExpressaoException {
		super(indice, comRetorno ? LocalContexto.INVOKE_LOCAL_CRET : LocalContexto.INVOKE_LOCAL_VOID);
		this.comRetorno = comRetorno;
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
		validar(funcaoValor, comRetorno);
		Funcao clone = funcaoValor.clonar();
		pilhaOperando.setArgumentos(clone);
		pilhaFuncao.push(clone);
		log(get() + get(nomeFuncoes, nomeLocal) + "] [funcao_valor->" + clone + "]", pilhaOperando);
	}

	private String get() {
		return "[" + (comRetorno ? LocalContexto.INVOKE_LOCAL_CRET : LocalContexto.INVOKE_LOCAL_VOID);
	}

	@Override
	public String toString() {
		return super.toString() + get(nomeFuncoes, nomeLocal);
	}
}