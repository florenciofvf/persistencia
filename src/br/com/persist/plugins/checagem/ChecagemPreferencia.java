package br.com.persist.plugins.checagem;

import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class ChecagemPreferencia {
	private static boolean exibirArqSentencas;

	private ChecagemPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		exibirArqSentencas = pref.getBoolean("checagem_exibir_arq_sentencas", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("checagem_exibir_arq_sentencas", exibirArqSentencas);
	}

	public static boolean isExibirArqSentencas() {
		return exibirArqSentencas;
	}

	public static void setExibirArqSentencas(boolean exibirArqSentencas) {
		ChecagemPreferencia.exibirArqSentencas = exibirArqSentencas;
	}
}