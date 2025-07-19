package br.com.persist.plugins.instrucao;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class InstrucaoPreferencia {
	private static int instrucaoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private InstrucaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		instrucaoPosicaoAbaFichario = pref.getInt("instrucao_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("instrucao_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("instrucao_posicao_aba_fichario", instrucaoPosicaoAbaFichario);
		pref.putBoolean("instrucao_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getInstrucaoPosicaoAbaFichario() {
		return instrucaoPosicaoAbaFichario;
	}

	public static void setInstrucaoPosicaoAbaFichario(int instrucaoPosicaoAbaFichario) {
		InstrucaoPreferencia.instrucaoPosicaoAbaFichario = instrucaoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		InstrucaoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}