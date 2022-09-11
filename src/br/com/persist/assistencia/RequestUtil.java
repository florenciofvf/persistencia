package br.com.persist.assistencia;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import br.com.persist.data.Objeto;
import br.com.persist.data.Texto;
import br.com.persist.data.Tipo;

public class RequestUtil {
	private RequestUtil() {
	}

	public static RequestResult processar(Objeto objeto) throws IOException {
		if (objeto != null) {
			Tipo tipoUrl = objeto.getValor("url");
			String url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
			if (Util.estaVazio(url)) {
				return null;
			}
			Map<String, String> requestHeader = getRequestHeader(objeto);
			String bodyParams = getBodyParams(objeto);
			return processar(url, requestHeader, bodyParams);
		}
		return null;
	}

	private static Map<String, String> getRequestHeader(Objeto objeto) {
		Tipo tipoHeader = objeto.getValor("header");
		if (tipoHeader instanceof Objeto) {
			Objeto objHeader = (Objeto) tipoHeader;
			return objHeader.getAtributosString();
		}
		return null;
	}

	private static String getBodyParams(Objeto objeto) {
		Tipo tipoBody = objeto.getValor("body");
		if (tipoBody instanceof Objeto) {
			Objeto objBody = (Objeto) tipoBody;
			Tipo params = objBody.getValor("parameters");
			return params instanceof Texto ? params.toString() : null;
		}
		return null;
	}

	private static RequestResult processar(String url, Map<String, String> requestHeader, String parameters)
			throws IOException {
		URL url2 = new URL(url);
		RequestResult result = new RequestResult();
		URLConnection conn = url2.openConnection();
		requestProperty(requestHeader, conn);
		String verbo = getVerbo(requestHeader, conn);
		checkDoOutput(parameters, conn, verbo);
		conn.connect();
		post(parameters, conn, verbo);
		result.setUrl(url);
		result.setHeaderFields(conn.getHeaderFields());
		result.setBytes(Util.getArrayBytes(conn.getInputStream()));
		return result;
	}

	private static void requestProperty(Map<String, String> header, URLConnection conn) {
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	public static String getVerbo(Map<String, String> header, URLConnection conn) {
		if (header != null) {
			return header.get("Request-Method");
		}
		return null;
	}

	private static void checkDoOutput(String parametros, URLConnection conn, String verbo) {
		if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
			conn.setDoOutput(true);
		}
	}

	private static void post(String parametros, URLConnection conn, String verbo) throws IOException {
		if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(parametros);
			osw.flush();
		}
	}
}