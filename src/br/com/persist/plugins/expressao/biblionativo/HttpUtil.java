package br.com.persist.plugins.expressao.biblionativo;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import br.com.persist.assistencia.Util;

public class HttpUtil {
	private static final String FORCE_CONTENT_TYPE = "forceContentType";
	private static final String HEADER_RESPONSE = "headerResponse";
	private static final String HEADER_REQUEST = "headerRequest";
	private static final String BYTES_RESPONSE = "bytesResponse";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CLEAR_COOKIE = "clearCookie";
	private static SSLSocketFactory defaultSSLSocketFactory;
	protected static final Logger LOG = Logger.getGlobal();
	private static final String EXCEPTION = "exception";
	private static CookieManager cookieManager;
	private static boolean checarTruster;

	private HttpUtil() {
	}

	public static void setCertificados(boolean b) {
		if (b) {
			HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory);
			return;
		}
		TrustManager[] array = new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				LOG.log(Level.FINEST, "checkServerTrusted");
				if (checarTruster && (chain == null || chain.length == 0)) {
					throw new CertificateException();
				}
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				LOG.log(Level.FINEST, "checkClientTrusted");
				if (checarTruster && (arg0 == null || arg0.length == 0)) {
					throw new CertificateException();
				}
			}
		} };
		try {
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, array, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (GeneralSecurityException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public static HttpResult get(Map<String, Object> param) {
		HttpResult result = new HttpResult();
		result.setRequest(param);
		String clearCookie = getClearCookie(param);
		if ("true".equals(clearCookie)) {
			removeAllCookie();
			return result;
		}
		try {
			URL url = new URL((String) param.get("url"));
			URLConnection conn = url.openConnection();
			configConnectionRedirects(conn);
			configHeaderRequest(param, conn);
			conn.connect();
			String forceContentType = getForceContentType(param);
			result.getResponse().put(HEADER_RESPONSE,
					getHeaderFields(conn.getHeaderFields(), forceContentType != null));
			checkForceContentType(result, forceContentType);
			result.getResponse().put(BYTES_RESPONSE, Util.getArrayBytes(conn.getInputStream()));
			configCookies(url, result);
		} catch (Exception ex) {
			result.getResponse().put(EXCEPTION, Util.getStackTrace("GET", ex));
		}
		return result;
	}

	private static void configCookies(URL url, HttpResult result) throws URISyntaxException {
		CookieStore cookieStore = cookieManager.getCookieStore();
		List<HttpCookie> list = cookieStore.get(url.toURI());
		for (HttpCookie item : list) {
			result.getCookies().add(item);
		}
	}

	private static void configConnectionRedirects(URLConnection conn) {
		if (conn instanceof HttpURLConnection) {
			((HttpURLConnection) conn).setInstanceFollowRedirects(false);
		}
	}

	public static boolean responseException(HttpResult result) {
		if (result == null) {
			return false;
		}
		Object object = result.getResponse().get(EXCEPTION);
		if (object == null) {
			return false;
		}
		String string = object.toString();
		return string != null && !string.trim().isEmpty();
	}

	@SuppressWarnings("unchecked")
	private static void configHeaderRequest(Map<String, Object> param, URLConnection conn) {
		Map<String, Object> header = (Map<String, Object>) param.get(HEADER_REQUEST);
		if (header != null) {
			for (Map.Entry<String, Object> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
			}
		}
	}

	public static HttpResult post(Map<String, Object> param) {
		HttpResult result = new HttpResult();
		result.setRequest(param);
		String clearCookie = getClearCookie(param);
		if ("true".equals(clearCookie)) {
			removeAllCookie();
			return result;
		}
		try {
			URL url = new URL((String) param.get("url"));
			URLConnection conn = url.openConnection();
			configHeaderRequest(param, conn);
			conn.setDoOutput(true);
			conn.connect();
			writer(param, conn);
			String forceContentType = getForceContentType(param);
			result.getResponse().put(HEADER_RESPONSE,
					getHeaderFields(conn.getHeaderFields(), forceContentType != null));
			checkForceContentType(result, forceContentType);
			result.getResponse().put(BYTES_RESPONSE, Util.getArrayBytes(conn.getInputStream()));
			configCookies(url, result);
		} catch (Exception ex) {
			result.getResponse().put(EXCEPTION, Util.getStackTrace("POST", ex));
		}
		return result;
	}

	private static String getForceContentType(Map<String, Object> param) {
		return (String) param.get(FORCE_CONTENT_TYPE);
	}

	private static String getClearCookie(Map<String, Object> param) {
		return (String) param.get(CLEAR_COOKIE);
	}

	@SuppressWarnings("unchecked")
	private static void checkForceContentType(HttpResult result, String forceContentType) {
		if (forceContentType == null) {
			return;
		}
		Map<String, List<String>> map = (Map<String, List<String>>) result.getResponse().get(HEADER_RESPONSE);
		if (map == null || map.isEmpty()) {
			map = new LinkedHashMap<>();
			result.getResponse().put(HEADER_RESPONSE, map);
			map.put(CONTENT_TYPE, Arrays.asList(forceContentType));
		} else {
			map.put(CONTENT_TYPE, Arrays.asList(forceContentType));
		}
	}

	private static void removeAllCookie() {
		CookieStore cookieStore = cookieManager.getCookieStore();
		cookieStore.removeAll();
	}

	@SuppressWarnings("unchecked")
	private static void writer(Map<String, Object> param, URLConnection conn) throws IOException {
		Map<String, String> parametros = (Map<String, String>) param.get("body");
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

	public static boolean isChecarTruster() {
		return checarTruster;
	}

	public static void setChecarTruster(boolean checarTruster) {
		HttpUtil.checarTruster = checarTruster;
	}

	private static Map<String, List<String>> getHeaderFields(Map<String, List<String>> mapa, boolean copiar) {
		if (!copiar) {
			return mapa;
		}
		Map<String, List<String>> resp = new LinkedHashMap<>();
		if (mapa != null) {
			for (Map.Entry<String, List<String>> entry : mapa.entrySet()) {
				String chave = entry.getKey();
				List<String> valor = entry.getValue();
				resp.put(chave, copy(valor));
			}
		}
		return resp;
	}

	private static List<String> copy(List<String> lista) {
		List<String> resp = new ArrayList<>();
		if (lista != null) {
			for (String item : lista) {
				resp.add(item);
			}
		}
		return resp;
	}

	static {
		defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}
}