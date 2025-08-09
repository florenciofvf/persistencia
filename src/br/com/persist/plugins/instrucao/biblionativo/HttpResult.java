package br.com.persist.plugins.instrucao.biblionativo;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResult {
	private Map<String, Object> request;
	private Map<String, Object> response;

	public Map<String, Object> getRequest() {
		if (request == null) {
			request = new LinkedHashMap<>();
		}
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public Map<String, Object> getResponse() {
		if (response == null) {
			response = new LinkedHashMap<>();
		}
		return response;
	}

	public void setResponse(Map<String, Object> response) {
		this.response = response;
	}
}