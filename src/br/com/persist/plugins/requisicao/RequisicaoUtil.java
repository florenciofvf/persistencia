package br.com.persist.plugins.requisicao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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

	public static InputStream requisicao(Tipo parametros, AtomicReference<Map<String, List<String>>> mapResponseHeader)
			throws IOException {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipoUrl = objeto.getValor("url");
			String url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
			Map<String, String> mapHeader = getMapHeader(objeto);
			String bodyParams = getBodyParams(objeto);
			return requisicao(url, mapHeader, bodyParams, mapResponseHeader);
		}
		return null;
	}

	public static Map<String, String> getMapHeader(Objeto objeto) {
		Map<String, String> mapHeader = null;
		Tipo tipoHeader = objeto.getValor("header");
		if (tipoHeader instanceof Objeto) {
			Objeto objHeader = (Objeto) tipoHeader;
			mapHeader = objHeader.getAtributosString();
		}
		return mapHeader;
	}

	public static String getBodyParams(Objeto objeto) {
		Tipo tipoBody = objeto.getValor("body");
		String bodyParams = null;
		if (tipoBody instanceof Objeto) {
			Objeto objBody = (Objeto) tipoBody;
			Tipo params = objBody.getValor("parameters");
			bodyParams = params instanceof Texto ? params.toString() : null;
		}
		return bodyParams;
	}

	public static InputStream requisicao(String url, Map<String, String> header, String parametros,
			AtomicReference<Map<String, List<String>>> mapResponseHeader) throws IOException {
		if (Util.estaVazio(url)) {
			return null;
		}
		URL url2 = new URL(url);
		URLConnection conn = url2.openConnection();
		String verbo = setRequestPropertyAndGetVerbo(header, conn);
		checarDoOutput(parametros, conn, verbo);
		conn.connect();
		sePost(parametros, conn, verbo);
		if (mapResponseHeader != null) {
			mapResponseHeader.set(conn.getHeaderFields());
		}
		return conn.getInputStream();
	}

	public static String setRequestPropertyAndGetVerbo(Map<String, String> header, URLConnection conn) {
		String verbo = null;
		if (header != null) {
			verbo = header.get("Request-Method");
			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return verbo;
	}

	public static void checarDoOutput(String parametros, URLConnection conn, String verbo) {
		if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
			conn.setDoOutput(true);
		}
	}

	public static void sePost(String parametros, URLConnection conn, String verbo) throws IOException {
		if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(parametros);
			osw.flush();
		}
	}
}