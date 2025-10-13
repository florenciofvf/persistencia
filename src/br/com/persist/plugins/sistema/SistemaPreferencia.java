package br.com.persist.plugins.sistema;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class SistemaPreferencia {
	private static int sistemaPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private SistemaPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		sistemaPosicaoAbaFichario = pref.getInt("sistema_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("sistema_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("sistema_posicao_aba_fichario", sistemaPosicaoAbaFichario);
		pref.putBoolean("sistema_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getSistemaPosicaoAbaFichario() {
		if (sistemaPosicaoAbaFichario == 0) {
			sistemaPosicaoAbaFichario = 1;
		}
		return sistemaPosicaoAbaFichario;
	}

	public static void setSistemaPosicaoAbaFichario(int sistemaPosicaoAbaFichario) {
		SistemaPreferencia.sistemaPosicaoAbaFichario = sistemaPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		SistemaPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}