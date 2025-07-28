package br.com.persist.plugins.entrega;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class EntregaPreferencia {
	private static int entregaPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private EntregaPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		entregaPosicaoAbaFichario = pref.getInt("entrega_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("entrega_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("entrega_posicao_aba_fichario", entregaPosicaoAbaFichario);
		pref.putBoolean("entrega_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getEntregaPosicaoAbaFichario() {
		if (entregaPosicaoAbaFichario == 0) {
			entregaPosicaoAbaFichario = 1;
		}
		return entregaPosicaoAbaFichario;
	}

	public static void setEntregaPosicaoAbaFichario(int entregaPosicaoAbaFichario) {
		EntregaPreferencia.entregaPosicaoAbaFichario = entregaPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		EntregaPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}