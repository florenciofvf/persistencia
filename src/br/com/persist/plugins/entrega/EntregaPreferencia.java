package br.com.persist.plugins.entrega;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class EntregaPreferencia {
	private static boolean exibirArqInvisivel;
	private static int entregaPosicaoAbaFichario;

	private EntregaPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		entregaPosicaoAbaFichario = pref.getInt("entrega_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqInvisivel = pref.getBoolean("entrega_exibir_arq_invisivel", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("entrega_posicao_aba_fichario", entregaPosicaoAbaFichario);
		pref.putBoolean("entrega_exibir_arq_invisivel", exibirArqInvisivel);
	}

	public static int getEntregaPosicaoAbaFichario() {
		return entregaPosicaoAbaFichario;
	}

	public static void setEntregaPosicaoAbaFichario(int entregaPosicaoAbaFichario) {
		EntregaPreferencia.entregaPosicaoAbaFichario = entregaPosicaoAbaFichario;
	}

	public static boolean isExibirArqInvisivel() {
		return exibirArqInvisivel;
	}

	public static void setExibirArqInvisivel(boolean exibirArqInvisivel) {
		EntregaPreferencia.exibirArqInvisivel = exibirArqInvisivel;
	}
}