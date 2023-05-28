package br.com.persist.plugins.instrucao;

import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class InstrucaoPreferencia {
	private InstrucaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
	}
}