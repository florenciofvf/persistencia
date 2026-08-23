package br.com.persist.plugins.expressao.biblionativo;

import java.util.LinkedHashMap;
import java.util.Map;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

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

	public String getDetalhes() {
		StringBuilder builder = new StringBuilder();
		HttpResult.detalhar("Request", builder, getRequest());
		builder.append(Constantes.QL2);
		HttpResult.detalhar("Response", builder, getResponse());
		return builder.toString();
	}

	public static void detalhar(String titulo, StringBuilder builder, Map<String, Object> mapa) {
		if (titulo == null || builder == null || mapa == null) {
			return;
		}
		builder.append(titulo + Constantes.QL);
		builder.append(Util.completar("", titulo.length(), '-') + Constantes.QL);
		append(0, builder, mapa);
	}

	@SuppressWarnings("unchecked")
	private static void append(int tab, StringBuilder builder, Map<String, Object> mapa) {
		for (Map.Entry<String, Object> entry : mapa.entrySet()) {
			String chave = entry.getKey();
			Object valor = entry.getValue();
			if (valor instanceof Map) {
				builder.append(Util.completar("", tab, '\t') + chave + ":" + Constantes.QL);
				append(tab + 1, builder, (Map<String, Object>) valor);
			} else {
				builder.append(Util.completar("", tab, '\t') + chave + ": " + valor + Constantes.QL);
			}
		}
	}
}