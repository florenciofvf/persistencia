package br.com.persist.plugins.instrucao.biblionativo;

import java.util.Map;

public class HttpResult {
	private Map<String, Object> request;
	private Map<String, Object> response;

	public Map<String, Object> getRequest() {
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public Map<String, Object> getResponse() {
		return response;
	}

	public void setResponse(Map<String, Object> response) {
		this.response = response;
	}
}