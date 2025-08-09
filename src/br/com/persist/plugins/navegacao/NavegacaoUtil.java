package br.com.persist.plugins.navegacao;

import java.util.List;

import br.com.persist.plugins.instrucao.biblionativo.HttpResult;

public class NavegacaoUtil {
	private NavegacaoUtil() {
	}

	public static boolean isHttpResult(List<Object> resp) {
		return resp != null && !resp.isEmpty() && resp.get(0) instanceof HttpResult;
	}
}