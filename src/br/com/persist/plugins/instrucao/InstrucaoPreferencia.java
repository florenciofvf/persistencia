package br.com.persist.plugins.instrucao;

import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class InstrucaoPreferencia {
	private static boolean exibirArqIgnorados;

	private InstrucaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		exibirArqIgnorados = pref.getBoolean("instrucao_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("instrucao_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		InstrucaoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}