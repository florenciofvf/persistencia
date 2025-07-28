package br.com.persist.plugins.legado;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class LegadoPreferencia {
	private static int legadoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private LegadoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		legadoPosicaoAbaFichario = pref.getInt("legado_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("legado_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("legado_posicao_aba_fichario", legadoPosicaoAbaFichario);
		pref.putBoolean("legado_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getLegadoPosicaoAbaFichario() {
		if (legadoPosicaoAbaFichario == 0) {
			legadoPosicaoAbaFichario = 1;
		}
		return legadoPosicaoAbaFichario;
	}

	public static void setLegadoPosicaoAbaFichario(int legadoPosicaoAbaFichario) {
		LegadoPreferencia.legadoPosicaoAbaFichario = legadoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		LegadoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}