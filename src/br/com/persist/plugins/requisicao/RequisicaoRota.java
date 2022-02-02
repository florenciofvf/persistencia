package br.com.persist.plugins.requisicao;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class RequisicaoRota {
	private final Map<String, String> rotas;

	public RequisicaoRota() {
		rotas = new HashMap<>();
	}

	public void limpar() {
		rotas.clear();
	}

	public String getValor(String chave) {
		return rotas.get(chave);
	}

	public String getStringRota(String string) {
		if (!Util.estaVazio(string)) {
			for (Map.Entry<String, String> entry : rotas.entrySet()) {
				String chave = entry.getKey();
				if (string.startsWith(chave)) {
					return chave;
				}
			}
		}
		return null;
	}

	public void adicionar(String chave, String valor) {
		if (!Util.estaVazio(chave)) {
			rotas.put(chave, valor);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : rotas.entrySet()) {
			String chave = entry.getKey();
			String valor = entry.getValue();
			if (sb.length() > 0) {
				sb.append(Constantes.QL2);
			}
			sb.append(chave + Constantes.QL);
			sb.append(valor + Constantes.QL);
		}
		return sb.toString();
	}
}