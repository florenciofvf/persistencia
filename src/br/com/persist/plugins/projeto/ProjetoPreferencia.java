package br.com.persist.plugins.projeto;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class ProjetoPreferencia {
	private static int projetoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private ProjetoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		projetoPosicaoAbaFichario = pref.getInt("projeto_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("projeto_exibir_arq_ignorados", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("projeto_posicao_aba_fichario", projetoPosicaoAbaFichario);
		pref.putBoolean("projeto_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getProjetoPosicaoAbaFichario() {
		return projetoPosicaoAbaFichario;
	}

	public static void setProjetoPosicaoAbaFichario(int projetoPosicaoAbaFichario) {
		ProjetoPreferencia.projetoPosicaoAbaFichario = projetoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		ProjetoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}