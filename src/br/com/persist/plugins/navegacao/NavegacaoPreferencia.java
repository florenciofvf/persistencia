package br.com.persist.plugins.navegacao;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class NavegacaoPreferencia {
	private static int navegacaoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private NavegacaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		navegacaoPosicaoAbaFichario = pref.getInt("navegacao_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("navegacao_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("navegacao_posicao_aba_fichario", navegacaoPosicaoAbaFichario);
		pref.putBoolean("navegacao_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getNavegacaoPosicaoAbaFichario() {
		if (navegacaoPosicaoAbaFichario == 0) {
			navegacaoPosicaoAbaFichario = 1;
		}
		return navegacaoPosicaoAbaFichario;
	}

	public static void setNavegacaoPosicaoAbaFichario(int navegacaoPosicaoAbaFichario) {
		NavegacaoPreferencia.navegacaoPosicaoAbaFichario = navegacaoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		NavegacaoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}