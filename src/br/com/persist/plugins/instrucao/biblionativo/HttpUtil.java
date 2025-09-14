package br.com.persist.plugins.instrucao.biblionativo;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import br.com.persist.assistencia.Util;

public class HttpUtil {
	protected static final Logger LOG = Logger.getGlobal();
	private static CookieManager cookieManager;

	private HttpUtil() {
	}

	public static void setCertificados(boolean b) {
		if (b) {
			HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
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
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				LOG.log(Level.FINEST, "checkClientTrusted");
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
		Map<String, Object> header = (Map<String, Object>) param.get("headerRequest");
		if (header != null) {
			for (Map.Entry<String, Object> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
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

	static {
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}
}