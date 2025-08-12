package br.com.persist.plugins.navegacao;

import java.util.List;
import java.util.Map;

import br.com.persist.plugins.instrucao.biblionativo.HttpResult;

public class NavegacaoUtil {
	private static final String HEADER_RESPONSE = "headerResponse";

	private NavegacaoUtil() {
	}

	public static boolean isHttpResult(List<Object> resp) {
		return resp != null && !resp.isEmpty() && resp.get(0) instanceof HttpResult;
	}

	@SuppressWarnings("unchecked")
	public static String getLocation(Map<String, Object> mapa) {
		if (mapa != null) {
			Object header = mapa.get(HEADER_RESPONSE);
			if (header instanceof Map) {
				return location((Map<String, List<String>>) header);
			}
		}
		return null;
	}

	private static String location(Map<String, List<String>> header) {
		List<String> list = header.get("Location");
		if (list == null) {
			list = header.get("location");
		}
		if (list == null) {
			list = header.get("LOCATION");
		}
		return getStringOuNull(list);
	}

	@SuppressWarnings("unchecked")
	public static String getCookie(Map<String, Object> mapa) {
		if (mapa != null) {
			Object header = mapa.get(HEADER_RESPONSE);
			if (header instanceof Map) {
				return cookie((Map<String, List<String>>) header);
			}
		}
		return null;
	}

	private static String cookie(Map<String, List<String>> header) {
		List<String> list = header.get("Set-Cookie");
		if (list == null) {
			list = header.get("set-cookie");
		}
		if (list == null) {
			list = header.get("SET-COOKIE");
		}
		return getStringOuNull(list);
	}

	@SuppressWarnings("unchecked")
	public static String getMimes(Map<String, Object> mapa) {
		if (mapa != null) {
			Object header = mapa.get(HEADER_RESPONSE);
			if (header instanceof Map) {
				return mimes((Map<String, List<String>>) header);
			}
		}
		return null;
	}

	private static String mimes(Map<String, List<String>> header) {
		List<String> list = header.get("Content-Type");
		if (list == null) {
			list = header.get("content-type");
		}
		if (list == null) {
			list = header.get("CONTENT-TYPE");
		}
		return getStringOuNull(list);
	}

	private static String getStringOuNull(List<String> list) {
		if (list != null) {
			StringBuilder builder = new StringBuilder();
			for (String item : list) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(item);
			}
			return builder.toString().trim();
		}
		return null;
	}
}