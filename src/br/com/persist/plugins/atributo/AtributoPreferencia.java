package br.com.persist.plugins.atributo;

import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class AtributoPreferencia {
	private static String dirPadraoSelecaoArquivos;
	private static int atributoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;

	private AtributoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		atributoPosicaoAbaFichario = pref.getInt("atributo_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("atributo_exibir_arq_ignorados", false);
		dirPadraoSelecaoArquivos = pref.get("atributo_dir_padrao_sel_arquivos", "");
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("atributo_posicao_aba_fichario", atributoPosicaoAbaFichario);
		pref.put("atributo_dir_padrao_sel_arquivos", dirPadraoSelecaoArquivos);
		pref.putBoolean("atributo_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getAtributoPosicaoAbaFichario() {
		if (atributoPosicaoAbaFichario == 0) {
			atributoPosicaoAbaFichario = 1;
		}
		return atributoPosicaoAbaFichario;
	}

	public static void setAtributoPosicaoAbaFichario(int atributoPosicaoAbaFichario) {
		AtributoPreferencia.atributoPosicaoAbaFichario = atributoPosicaoAbaFichario;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		AtributoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}

	public static String getDirPadraoSelecaoArquivos() {
		return dirPadraoSelecaoArquivos;
	}

	public static void setDirPadraoSelecaoArquivos(String dirPadraoSelecaoArquivos) {
		AtributoPreferencia.dirPadraoSelecaoArquivos = dirPadraoSelecaoArquivos;
	}
}