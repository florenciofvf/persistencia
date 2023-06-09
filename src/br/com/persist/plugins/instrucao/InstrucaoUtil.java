package br.com.persist.plugins.instrucao;

import java.math.BigDecimal;
import java.math.BigInteger;

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

	public static void checarNumber(Object obj) throws InstrucaoException {
		if (!(obj instanceof Number)) {
			throw new InstrucaoException("erro.valor_nao_number", obj);
		}
	}

	public static void checarBigIntegerBigDecimal(Object obj) throws InstrucaoException {
		if (!(obj instanceof BigInteger) && !(obj instanceof BigDecimal)) {
			throw new InstrucaoException("erro.valor_nao_bigi_bigd", obj);
		}
	}
}