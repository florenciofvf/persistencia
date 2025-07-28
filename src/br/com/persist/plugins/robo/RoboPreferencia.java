package br.com.persist.plugins.robo;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class RoboPreferencia {
	private static int roboPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private RoboPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		roboPosicaoAbaFichario = pref.getInt("robo_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("robo_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("robo_posicao_aba_fichario", roboPosicaoAbaFichario);
		pref.putBoolean("robo_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getRoboPosicaoAbaFichario() {
		if (roboPosicaoAbaFichario == 0) {
			roboPosicaoAbaFichario = 1;
		}
		return roboPosicaoAbaFichario;
	}

	public static void setRoboPosicaoAbaFichario(int roboPosicaoAbaFichario) {
		RoboPreferencia.roboPosicaoAbaFichario = roboPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		RoboPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}