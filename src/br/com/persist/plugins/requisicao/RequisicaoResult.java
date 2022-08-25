package br.com.persist.plugins.requisicao;

import java.util.List;
import java.util.Map;

public class RequisicaoResult {
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
}