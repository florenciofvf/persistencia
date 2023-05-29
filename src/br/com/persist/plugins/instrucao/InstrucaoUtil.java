package br.com.persist.plugins.instrucao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import br.com.persist.assistencia.ListaEncadeada;

public class InstrucaoUtil {
	private InstrucaoUtil() {
	}

	public static void checarOperando(Object obj) throws InstrucaoException {
		if (obj == null) {
			throw new InstrucaoException("erro.valor_invalido_operando_null");
		}
		if (!tipoValido(obj)) {
			throw new InstrucaoException("erro.valor_invalido_operando", obj);
		}
	}

	public static boolean tipoValido(Object valor) {
		return (valor instanceof String) || (valor instanceof BigInteger) || (valor instanceof BigDecimal)
				|| (valor instanceof ListaEncadeada<?>) || (valor instanceof Map<?, ?>);
	}
}