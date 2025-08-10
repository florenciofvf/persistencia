package br.com.persist.plugins.instrucao.biblionativo;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import br.com.persist.assistencia.Util;

public class HttpUtil {
	private HttpUtil() {
	}

	public static HttpResult get(Map<String, Object> param) {
		HttpResult result = new HttpResult();
		result.setRequest(param);
		try {
			URL url = new URL((String) param.get("url"));
			URLConnection conn = url.openConnection();
			configHeader(param, conn);
			conn.connect();
			result.getResponse().put("headerResponse", conn.getHeaderFields());
			result.getResponse().put("bytesResponse", Util.getArrayBytes(conn.getInputStream()));
		} catch (Exception ex) {
			result.getResponse().put("exception", Util.getStackTrace("GET", ex));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static void configHeader(Map<String, Object> param, URLConnection conn) {
		Map<String, String> header = (Map<String, String>) param.get("headerRequest");
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	public static HttpResult post(Map<String, Object> param) {
		HttpResult result = new HttpResult();
		result.setRequest(param);
		try {
			URL url = new URL((String) param.get("url"));
			URLConnection conn = url.openConnection();
			configHeader(param, conn);
			conn.setDoOutput(true);
			conn.connect();
			writer(param, conn);
			result.getResponse().put("headerResponse", conn.getHeaderFields());
			result.getResponse().put("bytesResponse", Util.getArrayBytes(conn.getInputStream()));
		} catch (Exception ex) {
			result.getResponse().put("exception", Util.getStackTrace("POST", ex));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static void writer(Map<String, Object> param, URLConnection conn) throws IOException {
		Map<String, String> parametros = (Map<String, String>) param.get("parametros");
		if (parametros != null) {
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(montarParametros(parametros));
			osw.flush();
		}
	}

	private static String montarParametros(Map<String, String> parametros) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : parametros.entrySet()) {
			if (builder.length() > 0) {
				builder.append("&");
			}
			String nome = entry.getKey();
			String valor = entry.getValue();
			builder.append(nome + "=" + valor);
		}
		return builder.toString();
	}
}