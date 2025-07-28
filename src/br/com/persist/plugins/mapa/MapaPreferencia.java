package br.com.persist.plugins.mapa;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class MapaPreferencia {
	private static int mapaPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private MapaPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		mapaPosicaoAbaFichario = pref.getInt("mapa_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("mapa_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("mapa_posicao_aba_fichario", mapaPosicaoAbaFichario);
		pref.putBoolean("mapa_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getMapaPosicaoAbaFichario() {
		if (mapaPosicaoAbaFichario == 0) {
			mapaPosicaoAbaFichario = 1;
		}
		return mapaPosicaoAbaFichario;
	}

	public static void setMapaPosicaoAbaFichario(int mapaPosicaoAbaFichario) {
		MapaPreferencia.mapaPosicaoAbaFichario = mapaPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		MapaPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}