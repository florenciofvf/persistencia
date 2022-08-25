package br.com.persist.plugins.requisicao;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.parser.Logico;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;

public class RequisicaoUtil {

	private RequisicaoUtil() {
	}

	public static List<String> getList(Map<String, List<String>> map) {
		List<String> list = map.get("Content-Type");
		if (list == null) {
			list = map.get("content-type");
		}
		if (list == null) {
			list = map.get("CONTENT-TYPE");
		}
		return list;
	}

	public static boolean getAutoSaveVar(Tipo parametros) {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipo = objeto.getValor("AutoSaveVar");
			String string = tipo instanceof Logico ? tipo.toString() : null;
			return Boolean.parseBoolean(string);
		}
		return false;
	}

	public static String getAtributoVarAuthToken(Tipo parametros) {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipo = objeto.getValor("SetVarAuthToken");
			return tipo instanceof Texto ? tipo.toString() : null;
		}
		return null;
	}

	public static String getAtributoVarCookie(Tipo parametros) {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipo = objeto.getValor("SetVarCookie");
			return tipo instanceof Texto ? tipo.toString() : null;
		}
		return null;
	}

	public static RequisicaoResult requisicao(Tipo request) throws IOException {
		if (request instanceof Objeto) {
			Objeto objeto = (Objeto) request;
			Tipo tipoUrl = objeto.getValor("url");
			String url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
			Map<String, String> requestHeader = getRequestHeader(objeto);
			String bodyParams = getBodyParams(objeto);
			return requisicao(url, requestHeader, bodyParams);
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

	private static RequisicaoResult requisicao(String url, Map<String, String> requestHeader, String parameters) throws IOException {
		if (Util.estaVazio(url)) {
			return null;
		}
		RequisicaoResult result = new RequisicaoResult();
		URL url2 = new URL(url);
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