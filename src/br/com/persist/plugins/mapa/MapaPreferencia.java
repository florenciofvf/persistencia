package br.com.persist.plugins.mapa;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class MapaPreferencia {
	private static boolean exibirArqInvisivel;
	private static int mapaPosicaoAbaFichario;

	private MapaPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		mapaPosicaoAbaFichario = pref.getInt("mapa_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqInvisivel = pref.getBoolean("mapa_exibir_arq_invisivel", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("mapa_posicao_aba_fichario", mapaPosicaoAbaFichario);
		pref.putBoolean("mapa_exibir_arq_invisivel", exibirArqInvisivel);
	}

	public static int getMapaPosicaoAbaFichario() {
		return mapaPosicaoAbaFichario;
	}

	public static void setMapaPosicaoAbaFichario(int mapaPosicaoAbaFichario) {
		MapaPreferencia.mapaPosicaoAbaFichario = mapaPosicaoAbaFichario;
	}

	public static boolean isExibirArqInvisivel() {
		return exibirArqInvisivel;
	}

	public static void setExibirArqInvisivel(boolean exibirArqInvisivel) {
		MapaPreferencia.exibirArqInvisivel = exibirArqInvisivel;
	}
}