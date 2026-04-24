package br.com.persist.plugins.expressao.invocacao;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class InvocacaoParamInstrucao extends Instrucao {
	private final boolean comRetorno;
	private String[] nomeFuncoes;
	private String nomeFuncao;

	public InvocacaoParamInstrucao(boolean comRetorno) {
		super(comRetorno ? InvocacaoContexto.INVOKE_PARAM_CRET : InvocacaoContexto.INVOKE_PARAM_VOID);
		this.comRetorno = comRetorno;
	}

	@Override
	public Instrucao novo() {
		return new InvocacaoParamInstrucao(comRetorno);
	}

	@Override
	public void setParametros(String parametros) {
		int pos = parametros.indexOf(' ');
		nomeFuncoes = parametros.substring(0, pos).split(CIFRAO);
		nomeFuncao = parametros.substring(pos + 1);
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
					throw new ExpressaoException("Funcao Parent nula", false);
				}
				checarNome(item, funcaoAlvo);
			}
		}

		if (funcaoAlvo == null) {
			throw new ExpressaoException("Funcao Alvo nula", false);
		}

		Object valor = funcaoAlvo.getValorParametro(nomeFuncao);

		if (valor == null) {
			throw new ExpressaoException("erro.valor_param", nomeFuncao);
		}

		if (!(valor instanceof Funcao)) {
			throw new ExpressaoException("erro.valor_param_nao_funcao", nomeFuncao, funcao.getNome(), valor.toString(),
					funcao.getBiblioteca().getNomeAbsoluto());
		}

		Funcao funcaoParam = (Funcao) valor;
		InvocacaoInstrucao.validar(funcaoParam, comRetorno);
		InvocacaoInstrucao.setArgumentos(funcaoParam, pilhaOperando);
		pilhaFuncao.push(funcaoParam);
	}
}