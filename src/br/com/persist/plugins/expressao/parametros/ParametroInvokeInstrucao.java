package br.com.persist.plugins.expressao.parametros;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.invocacao.Invoke;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

/**
 * <pre>
*defun proxy(funcao) {
*	return funcao();
*}
*
*defun invocaFuncao(funcao) void {
*	funcao();
*	return;
*}
 * </pre>
 **/
public class ParametroInvokeInstrucao extends Invoke implements br.com.persist.plugins.expressao.processador.Invoke {
	private final boolean comRetorno;
	private String[] nomeFuncoes;
	private String nomeFuncao;

	public ParametroInvokeInstrucao(boolean comRetorno, int indice, String parametros) throws ExpressaoException {
		super(indice, comRetorno ? ParametroContexto.INVOKE_PARAM_CRET : ParametroContexto.INVOKE_PARAM_VOID);
		this.comRetorno = comRetorno;
		int pos = parametros.indexOf(' ');
		nomeFuncoes = parametros.substring(0, pos).split(CIFRAO);
		nomeFuncao = parametros.substring(pos + 1);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Funcao funcaoAlvo = getFuncaoAlvo(funcao, nomeFuncoes);
		Object valor = funcaoAlvo.getValorParametro(nomeFuncao);
		if (valor == null) {
			throw new ExpressaoException("erro.valor_param", nomeFuncao);
		}
		if (!(valor instanceof Funcao)) {
			throw new ExpressaoException("erro.valor_param_nao_funcao", nomeFuncao, funcao.getNome(), valor.toString(),
					funcao.getBiblioteca().getNomeAbsoluto());
		}
		Funcao funcaoValor = (Funcao) valor;
		Funcao funcaoLoad = funcaoValor.clonar();
		validar(funcaoLoad, comRetorno);
		pilhaOperando.setArgumentos(funcaoLoad);
		pilhaFuncao.push(funcaoLoad);
		log(get() + get(nomeFuncoes, nomeFuncao) + "] [funcao_alvo->" + funcaoAlvo + "] [valor->" + funcaoLoad + "]",
				pilhaOperando);
	}

	private String get() {
		return "[" + (comRetorno ? ParametroContexto.INVOKE_PARAM_CRET : ParametroContexto.INVOKE_PARAM_VOID);
	}

	@Override
	public String toString() {
		return super.toString() + get(nomeFuncoes, nomeFuncao);
	}
}