package br.com.persist.plugins.instrucao.processador;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.biblionativo.Lista;

public class InstrucaoUtil {
	private InstrucaoUtil() {
	}

	public static void checarParametro(Object obj) throws InstrucaoException {
		if (obj == null) {
			throw new InstrucaoException("erro.valor_invalido_param_null");
		}
	}

	public static void checarOperando(Object obj) throws InstrucaoException {
		if (obj == null) {
			throw new InstrucaoException("erro.valor_invalido_operando_null");
		}
	}

	public static void checarFuncao(Object obj) throws InstrucaoException {
		if (obj == null) {
			throw new InstrucaoException("erro.valor_invalido_funcao_null");
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