package br.com.persist.plugins.checagem;

import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class ChecagemPreferencia {
	private static boolean exibirArqSentencas;
	private static boolean exibirArqIgnorados;

	private ChecagemPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		exibirArqSentencas = pref.getBoolean("checagem_exibir_arq_sentencas", false);
		exibirArqIgnorados = pref.getBoolean("checagem_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("checagem_exibir_arq_sentencas", exibirArqSentencas);
		pref.putBoolean("checagem_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static boolean isExibirArqSentencas() {
		return exibirArqSentencas;
	}

	public static void setExibirArqSentencas(boolean exibirArqSentencas) {
		ChecagemPreferencia.exibirArqSentencas = exibirArqSentencas;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		ChecagemPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}