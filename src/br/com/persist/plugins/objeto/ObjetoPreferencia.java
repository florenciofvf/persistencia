package br.com.persist.plugins.objeto;

import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class ObjetoPreferencia {
	private static boolean habilitadoInnerJoinsObjeto;

	private ObjetoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		habilitadoInnerJoinsObjeto = pref.getBoolean("habilitado_inner_joins_objeto", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("habilitado_inner_joins_objeto", habilitadoInnerJoinsObjeto);
	}

	public static boolean isHabilitadoInnerJoinsObjeto() {
		return habilitadoInnerJoinsObjeto;
	}

	public static void setHabilitadoInnerJoinsObjeto(boolean habilitadoInnerJoinsObjeto) {
		ObjetoPreferencia.habilitadoInnerJoinsObjeto = habilitadoInnerJoinsObjeto;
	}
}