package br.com.persist.plugins.anotacao;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class AnotacaoPreferencia {
	private static int anotacaoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private AnotacaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		anotacaoPosicaoAbaFichario = pref.getInt("anotacao_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("anotacao_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("anotacao_posicao_aba_fichario", anotacaoPosicaoAbaFichario);
		pref.putBoolean("anotacao_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getAnotacaoPosicaoAbaFichario() {
		return anotacaoPosicaoAbaFichario;
	}

	public static void setAnotacaoPosicaoAbaFichario(int anotacaoPosicaoAbaFichario) {
		AnotacaoPreferencia.anotacaoPosicaoAbaFichario = anotacaoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		AnotacaoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}
