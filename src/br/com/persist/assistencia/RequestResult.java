package br.com.persist.assistencia;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class RequestResult {
	private Map<String, List<String>> headerFields;
	private byte[] bytes;
	private String url;

	public Map<String, List<String>> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(Map<String, List<String>> headerFields) {
		this.headerFields = headerFields;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getString() {
		return new String(bytes);
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}

	public String getMime() {
		if (headerFields != null) {
			List<String> list = getList(headerFields);
			if (list != null && !list.isEmpty()) {
				return get(list);
			}
		}
		return null;
	}

	public String getCookie() {
		if (headerFields != null) {
			List<String> list = headerFields.get("Set-Cookie");
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

	private List<String> getList(Map<String, List<String>> map) {
		List<String> list = map.get("Content-Type");
		if (list == null) {
			list = map.get("content-type");
		}
		if (list == null) {
			list = map.get("CONTENT-TYPE");
		}
		return list;
	}

	private String get(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String string : list) {
			if (!Util.estaVazio(string)) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(string.trim());
			}
		}
		return sb.toString().trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("url=" + url);
		if (bytes == null) {
			sb.append(" bytes=[]");
		} else {
			sb.append(Constantes.QL);
			sb.append(new String(bytes));
		}
		return sb.toString();
	}
}