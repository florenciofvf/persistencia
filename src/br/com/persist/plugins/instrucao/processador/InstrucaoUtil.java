package br.com.persist.plugins.instrucao.inst;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.assistencia.Lista;
import br.com.persist.plugins.instrucao.InstrucaoException;

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

	public static void checarLista(Object obj) throws InstrucaoException {
		if (!(obj instanceof Lista)) {
			throw new InstrucaoException("erro.valor_nao_lista", obj);
		}
	}

	public static void checarString(Object obj) throws InstrucaoException {
		if (!(obj instanceof String)) {
			throw new InstrucaoException("erro.valor_nao_string", obj);
		}
	}

	public static void checarBigIntegerBigDecimal(Object obj) throws InstrucaoException {
		if (!(obj instanceof BigInteger) && !(obj instanceof BigDecimal)) {
			throw new InstrucaoException("erro.valor_nao_bigi_bigd", obj);
		}
	}
}