package br.com.persist.plugins.execucao;

import java.util.prefs.Preferences;

import br.com.persist.assistencia.SwingConstantes;
import br.com.persist.formulario.Formulario;

public class ExecucaoPreferencia {
	private static int execucaoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private ExecucaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		execucaoPosicaoAbaFichario = pref.getInt("execucao_posicao_aba_fichario", SwingConstantes.TOP);
		exibirArqIgnorados = pref.getBoolean("execucao_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("execucao_posicao_aba_fichario", execucaoPosicaoAbaFichario);
		pref.putBoolean("execucao_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getExecucaoPosicaoAbaFichario() {
		if (execucaoPosicaoAbaFichario == 0) {
			execucaoPosicaoAbaFichario = 1;
		}
		return execucaoPosicaoAbaFichario;
	}

	public static void setExecucaoPosicaoAbaFichario(int execucaoPosicaoAbaFichario) {
		ExecucaoPreferencia.execucaoPosicaoAbaFichario = execucaoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		ExecucaoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}
