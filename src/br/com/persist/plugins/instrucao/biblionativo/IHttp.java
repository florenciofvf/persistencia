package br.com.persist.plugins.instrucao.biblionativo;

import java.util.Map;

public class IHttp {
	private IHttp() {
	}

	@Biblio(0)
	@SuppressWarnings("unchecked")
	public static HttpResult get(Object param) {
		if (param instanceof Map<?, ?>) {
			return HttpUtil.get((Map<String, Object>) param);
		}
		return null;
	}

	@Biblio(1)
	@SuppressWarnings("unchecked")
	public static HttpResult post(Object param) {
		if (param instanceof Map<?, ?>) {
			return HttpUtil.post((Map<String, Object>) param);
		}
		return null;
	}
}