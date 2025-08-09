package br.com.persist.plugins.navegacao;

import java.util.ArrayList;
import java.util.List;

public class NavegacaoProvedor {
	private static final List<Navegacao> lista = new ArrayList<>();

	private NavegacaoProvedor() {
	}

	public static Navegacao getNavegacao(String nome) {
		for (Navegacao obj : lista) {
			if (obj.getNome().equals(nome)) {
				return obj;
			}
		}
		return null;
	}
}