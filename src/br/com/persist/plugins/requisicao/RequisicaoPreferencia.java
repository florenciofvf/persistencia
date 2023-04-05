package br.com.persist.plugins.requisicao;

import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class RequisicaoPreferencia {
	private static boolean exibirArqIgnorados;
	private static boolean abrirModoTabela;
	private static boolean exibirArqMimes;

	private RequisicaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		exibirArqIgnorados = pref.getBoolean("requisicao_exibir_arq_ignorados", false);
		abrirModoTabela = pref.getBoolean("requisicao_abrir_modo_tabela", false);
		exibirArqMimes = pref.getBoolean("requisicao_exibir_arq_mimes", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("requisicao_exibir_arq_ignorados", exibirArqIgnorados);
		pref.putBoolean("requisicao_abrir_modo_tabela", abrirModoTabela);
		pref.putBoolean("requisicao_exibir_arq_mimes", exibirArqMimes);
	}

	public static boolean isAbrirModoTabela() {
		return abrirModoTabela;
	}

	public static void setAbrirModoTabela(boolean abrirModoTabela) {
		RequisicaoPreferencia.abrirModoTabela = abrirModoTabela;
	}

	public static boolean isExibirArqMimes() {
		return exibirArqMimes;
	}

	public static void setExibirArqMimes(boolean exibirArqMimes) {
		RequisicaoPreferencia.exibirArqMimes = exibirArqMimes;
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		RequisicaoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}
}