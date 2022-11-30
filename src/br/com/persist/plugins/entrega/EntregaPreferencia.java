package br.com.persist.plugins.entrega;

import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class EntregaPreferencia {
	private static boolean exibirArqInvisivel;

	private EntregaPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		exibirArqInvisivel = pref.getBoolean("entrega_exibir_arq_invisivel", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("entrega_exibir_arq_invisivel", exibirArqInvisivel);
	}

	public static boolean isExibirArqInvisivel() {
		return exibirArqInvisivel;
	}

	public static void setExibirArqInvisivel(boolean exibirArqInvisivel) {
		EntregaPreferencia.exibirArqInvisivel = exibirArqInvisivel;
	}
}