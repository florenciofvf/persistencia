package br.com.persist.plugins.atributo;

import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class AtributoPreferencia {
	private static boolean exibirArqIgnorados;

	private AtributoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		exibirArqIgnorados = pref.getBoolean("atributo_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("atributo_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		AtributoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}