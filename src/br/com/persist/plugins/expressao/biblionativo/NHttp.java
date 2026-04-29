package br.com.persist.plugins.expressao.biblionativo;

import java.util.Map;

public class NHttp {
	private NHttp() {
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