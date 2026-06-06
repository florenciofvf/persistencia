package br.com.persist.plugins.projeto;

import java.awt.Color;
import java.util.prefs.Preferences;

import javax.swing.SwingConstants;

import br.com.persist.formulario.Formulario;

public class ProjetoPreferencia {
	private static String dirPadraoSelecaoArquivos;
	private static int projetoPosicaoAbaFichario;
	private static boolean exibirArqIgnorados;
	private static Color corElementoFinalRest;
	private static Color corElementoFinalView;

	private ProjetoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		corElementoFinalRest = new Color(pref.getInt("cor_elemento_final_rest", Color.BLACK.getRGB()));
		corElementoFinalView = new Color(pref.getInt("cor_elemento_final_view", Color.BLACK.getRGB()));
		projetoPosicaoAbaFichario = pref.getInt("projeto_posicao_aba_fichario", SwingConstants.TOP);
		exibirArqIgnorados = pref.getBoolean("projeto_exibir_arq_ignorados", false);
		dirPadraoSelecaoArquivos = pref.get("projeto_dir_padrao_sel_arquivos", "");
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("projeto_posicao_aba_fichario", projetoPosicaoAbaFichario);
		pref.putInt("cor_elemento_final_rest", corElementoFinalRest.getRGB());
		pref.putInt("cor_elemento_final_view", corElementoFinalView.getRGB());
		pref.put("projeto_dir_padrao_sel_arquivos", dirPadraoSelecaoArquivos);
		pref.putBoolean("projeto_exibir_arq_ignorados", exibirArqIgnorados);
	}

	public static int getProjetoPosicaoAbaFichario() {
		if (projetoPosicaoAbaFichario == 0) {
			projetoPosicaoAbaFichario = 1;
		}
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

	public static Color getCorElementoFinalRest() {
		return corElementoFinalRest;
	}

	public static void setCorElementoFinalRest(Color corElementoFinalRest) {
		ProjetoPreferencia.corElementoFinalRest = corElementoFinalRest;
	}

	public static Color getCorElementoFinalView() {
		return corElementoFinalView;
	}

	public static void setCorElementoFinalView(Color corElementoFinalView) {
		ProjetoPreferencia.corElementoFinalView = corElementoFinalView;
	}

	public static String getDirPadraoSelecaoArquivos() {
		return dirPadraoSelecaoArquivos;
	}

	public static void setDirPadraoSelecaoArquivos(String dirPadraoSelecaoArquivos) {
		ProjetoPreferencia.dirPadraoSelecaoArquivos = dirPadraoSelecaoArquivos;
	}
}