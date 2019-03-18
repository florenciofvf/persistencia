package br.com.persist.util;

import java.util.prefs.Preferences;

import br.com.persist.Objeto;

public class Preferencias {
	public static boolean area_trans_tabela_registros;
	public static boolean copiar_nome_coluna_listener;
	public static boolean fichario_com_rolagem;
	public static boolean abrir_auto_destacado;
	public static boolean fechar_apos_soltar;
	public static boolean abrir_auto;

	private Preferencias() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Objeto.class);

		area_trans_tabela_registros = pref.getBoolean("area_trans_tabela_registros", false);
		copiar_nome_coluna_listener = pref.getBoolean("copiar_nome_coluna_listener", false);
		abrir_auto_destacado = pref.getBoolean("abrir_auto_destacado", false);
		fichario_com_rolagem = pref.getBoolean("fichario_com_rolagem", true);
		fechar_apos_soltar = pref.getBoolean("fechar_apos_soltar", true);
		abrir_auto = pref.getBoolean("abrir_auto", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Objeto.class);

		pref.putBoolean("area_trans_tabela_registros", area_trans_tabela_registros);
		pref.putBoolean("copiar_nome_coluna_listener", copiar_nome_coluna_listener);
		pref.putBoolean("abrir_auto_destacado", abrir_auto_destacado);
		pref.putBoolean("fichario_com_rolagem", fichario_com_rolagem);
		pref.putBoolean("fechar_apos_soltar", fechar_apos_soltar);
		pref.putBoolean("abrir_auto", abrir_auto);
	}
}