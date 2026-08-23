package br.com.persist.plugins.navegacao;

import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.biblionativo.HttpResult;

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

	@SuppressWarnings("unchecked")
	public static String getStatus(Map<String, Object> mapa) {
		if (mapa != null) {
			Object header = mapa.get(HEADER_RESPONSE);
			if (header instanceof Map) {
				return status((Map<String, List<String>>) header);
			}
		}
		return null;
	}

	private static String status(Map<String, List<String>> header) {
		for (Map.Entry<String, List<String>> entry : header.entrySet()) {
			String chave = entry.getKey();
			Object valor = entry.getValue();
			if (chave == null) {
				String resp = getHttpStatus(valor);
				if (resp != null) {
					return resp;
				}
			}
		}
		return "";
	}

	private static String getHttpStatus(Object valor) {
		if (valor == null) {
			return null;
		}
		String string = valor.toString();
		if (string.contains("HTTP") && string.contains("/")) {
			return string;
		}
		return null;
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

	public static String getBase(Object obj) {
		if (obj == null) {
			return null;
		}
		String string = obj.toString();
		int pos = string.indexOf("://");
		if (pos == -1) {
			return null;
		}
		pos = string.indexOf("/", pos + 3);
		if (pos == -1) {
			return string;
		}
		return string.substring(0, pos);
	}

	public static String normalBarra(String base, String complemento) {
		if (base == null || complemento == null) {
			return "";
		}
		base = base.trim();
		complemento = complemento.trim();
		if (base.endsWith("/") || complemento.startsWith("/")) {
			return "";
		}
		return "/";
	}
}