package br.com.persist.plugins.expressao.parametros;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoUtil;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Load;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ParametroLoadInstrucao extends Instrucao implements Load {
	private String[] nomeFuncoes;
	private String nomeParametro;

	public ParametroLoadInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, ParametroContexto.LOAD_PARAM);
		int pos = parametros.indexOf(' ');
		nomeFuncoes = parametros.substring(0, pos).split(CIFRAO);
		nomeParametro = parametros.substring(pos + 1);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Funcao funcaoAlvo = null;

		for (String item : nomeFuncoes) {
			if (funcaoAlvo == null) {
				checarNome(item, funcao);
				funcaoAlvo = funcao;
			} else {
				funcaoAlvo = funcaoAlvo.getParent();
				if (funcaoAlvo == null) {
					throw new ExpressaoException("Funcao Parent nula: " + item, false);
				}
				checarNome(item, funcaoAlvo);
			}
		}

		if (funcaoAlvo == null) {
			throw new ExpressaoException("Funcao Alvo nula", false);
		}

		Object valor = funcaoAlvo.getValorParametro(nomeParametro);
		if (ExpressaoConstantes.DEBUG) {
			ExpressaoUtil.print("######### (funcao alvo) #########", funcaoAlvo);
		}
		pilhaOperando.push(valor);
	}

	@Override
	public String toString() {
		return super.toString() + " " + get(nomeFuncoes) + "." + nomeParametro;
	}
}