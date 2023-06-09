package br.com.persist.plugins.instrucao;

public class InstrucaoUtil {
	private InstrucaoUtil() {
	}

	public static void checarParam(Object obj) throws InstrucaoException {
		if (obj == null) {
			throw new InstrucaoException("erro.valor_invalido_param_null");
		}
	}

	public static void checarOperando(Object obj) throws InstrucaoException {
		if (obj == null) {
			throw new InstrucaoException("erro.valor_invalido_operando_null");
		}
	}

	public static void checarMetodo(Object obj) throws InstrucaoException {
		if (obj == null) {
			throw new InstrucaoException("erro.valor_invalido_metodo_null");
		}
	}
}