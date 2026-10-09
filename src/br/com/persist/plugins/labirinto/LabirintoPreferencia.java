package br.com.persist.plugins.labirinto;

import java.util.prefs.Preferences;

import br.com.persist.assistencia.SwingConstantes;
import br.com.persist.formulario.Formulario;

public class LabirintoPreferencia {
	private static int labirintoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private LabirintoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		labirintoPosicaoAbaFichario = pref.getInt("labirinto_posicao_aba_fichario", SwingConstantes.TOP);
		exibirArqIgnorados = pref.getBoolean("labirinto_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("labirinto_posicao_aba_fichario", labirintoPosicaoAbaFichario);
		pref.putBoolean("labirinto_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getLabirintoPosicaoAbaFichario() {
		if (labirintoPosicaoAbaFichario == 0) {
			labirintoPosicaoAbaFichario = 1;
		}
		return labirintoPosicaoAbaFichario;
	}

	public static void setLabirintoPosicaoAbaFichario(int labirintoPosicaoAbaFichario) {
		LabirintoPreferencia.labirintoPosicaoAbaFichario = labirintoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		LabirintoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}