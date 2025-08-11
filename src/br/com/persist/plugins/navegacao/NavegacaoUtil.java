package br.com.persist.plugins.navegacao;

import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.biblionativo.HttpResult;

public class NavegacaoUtil {
	private NavegacaoUtil() {
	}

	public static boolean isHttpResult(List<Object> resp) {
		return resp != null && !resp.isEmpty() && resp.get(0) instanceof HttpResult;
	}

	@SuppressWarnings("unchecked")
	public static String getMimes(Map<String, Object> mapa) {
		if (mapa == null) {
			return "";
		}
		Object header = mapa.get("headerResponse");
		if (header instanceof Map) {
			return mimes((Map<String, List<String>>) header);
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public static String getCookie(Map<String, Object> mapa) {
		if (mapa == null) {
			return "";
		}
		Object header = mapa.get("headerResponse");
		if (header instanceof Map) {
			return cookie((Map<String, List<String>>) header);
		}
		return "";
	}

	private static String cookie(Map<String, List<String>> header) {
		List<String> list = header.get("Set-Cookie");
		if (list == null) {
			list = header.get("set-cookie");
		}
		if (list == null) {
			list = header.get("SET-COOKIE");
		}
		if (list != null && !list.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (String item : list) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(item);
			}
			return builder.toString();
		}
		return null;
	}

	private static String mimes(Map<String, List<String>> header) {
		List<String> list = getList(header);
		if (list != null && !list.isEmpty()) {
			return get(list);
		}
		return "";
	}

	private static List<String> getList(Map<String, List<String>> map) {
		List<String> list = map.get("Content-Type");
		if (list == null) {
			list = map.get("content-type");
		}
		if (list == null) {
			list = map.get("CONTENT-TYPE");
		}
		return list;
	}

	private static String get(List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (String item : list) {
			if (!Util.isEmpty(item)) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(item.trim());
			}
		}
		return builder.toString().trim();
	}
}