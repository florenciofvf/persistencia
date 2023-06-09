package br.com.persist.plugins.instrucao;

public class InstrucaoUtil {
	private InstrucaoUtil() {
	}

	public static void checarOperando(Object obj) throws InstrucaoException {
		if (obj == null) {
			throw new InstrucaoException("erro.valor_invalido_operando_null");
		}
	}
}