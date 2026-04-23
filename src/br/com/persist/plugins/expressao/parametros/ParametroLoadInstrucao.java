package br.com.persist.plugins.expressao.parametros;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ParametroLoadInstrucao extends Instrucao {
	private String[] nomeFuncoes;
	private String nomeParametro;

	public ParametroLoadInstrucao() {
		super(ParametroContexto.LOAD_PARAM);
	}

	@Override
	public Instrucao novo() {
		return new ParametroLoadInstrucao();
	}

	@Override
	public void setParametros(String parametros) {
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
				validarNome(item, funcao.getNome());
				funcaoAlvo = funcao;
			} else {
				funcaoAlvo = funcaoAlvo.getParent();
				if (funcaoAlvo == null) {
					throw new ExpressaoException("Funcao Parent nula", false);
				}
				validarNome(item, funcaoAlvo.getNome());
			}
		}

		if (funcaoAlvo == null) {
			throw new ExpressaoException("Funcao Alvo nula", false);
		}

		Object valor = funcaoAlvo.getValorParametro(nomeParametro);
		pilhaOperando.push(valor);
	}

	private void validarNome(String nomeFuncaoParam, String nomeFuncaoExec) throws ExpressaoException {
		if (!nomeFuncaoParam.equals(nomeFuncaoExec)) {
			throw new ExpressaoException("Nomes diferentes: " + nomeFuncaoParam + " >>> " + nomeFuncaoExec);
		}
	}
}