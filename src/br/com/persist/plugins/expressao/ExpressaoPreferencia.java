package br.com.persist.plugins.expressao;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class ExpressaoPreferencia {
	private static int expressaoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private ExpressaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		expressaoPosicaoAbaFichario = pref.getInt("expressao_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("expressao_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("expressao_posicao_aba_fichario", expressaoPosicaoAbaFichario);
		pref.putBoolean("expressao_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getExpressaoPosicaoAbaFichario() {
		if (expressaoPosicaoAbaFichario == 0) {
			expressaoPosicaoAbaFichario = 1;
		}
		return expressaoPosicaoAbaFichario;
	}

	public static void setExpressaoPosicaoAbaFichario(int expressaoPosicaoAbaFichario) {
		ExpressaoPreferencia.expressaoPosicaoAbaFichario = expressaoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		ExpressaoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}