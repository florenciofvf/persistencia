package br.com.persist.util;

import java.util.prefs.Preferences;

import br.com.persist.Objeto;

public class Preferencias {
	private static boolean areaTransTabelaRegistros;
	private static boolean copiarNomeColunaListener;
	private static boolean ficharioComRolagem;
	private static boolean abrirAutoDestacado;
	private static int intervaloPesquisaAuto;
	private static boolean fecharAposSoltar;
	private static int posicaoAbaFichario;
	private static boolean abrirAuto;

	private Preferencias() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Objeto.class);

		areaTransTabelaRegistros = pref.getBoolean("area_trans_tabela_registros", false);
		copiarNomeColunaListener = pref.getBoolean("copiar_nome_coluna_listener", false);
		intervaloPesquisaAuto = pref.getInt("intervalo_pesquisa_auto", 5000);
		abrirAutoDestacado = pref.getBoolean("abrir_auto_destacado", false);
		ficharioComRolagem = pref.getBoolean("fichario_com_rolagem", true);
		fecharAposSoltar = pref.getBoolean("fechar_apos_soltar", true);
		posicaoAbaFichario = pref.getInt("posicao_aba_fichario", 1);
		abrirAuto = pref.getBoolean("abrir_auto", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Objeto.class);

		pref.putBoolean("area_trans_tabela_registros", areaTransTabelaRegistros);
		pref.putBoolean("copiar_nome_coluna_listener", copiarNomeColunaListener);
		pref.putInt("intervalo_pesquisa_auto", intervaloPesquisaAuto);
		pref.putBoolean("abrir_auto_destacado", abrirAutoDestacado);
		pref.putBoolean("fichario_com_rolagem", ficharioComRolagem);
		pref.putBoolean("fechar_apos_soltar", fecharAposSoltar);
		pref.putInt("posicao_aba_fichario", posicaoAbaFichario);
		pref.putBoolean("abrir_auto", abrirAuto);
	}

	public static boolean isAreaTransTabelaRegistros() {
		return areaTransTabelaRegistros;
	}

	public static void setAreaTransTabelaRegistros(boolean areaTransTabelaRegistros) {
		Preferencias.areaTransTabelaRegistros = areaTransTabelaRegistros;
	}

	public static boolean isCopiarNomeColunaListener() {
		return copiarNomeColunaListener;
	}

	public static void setCopiarNomeColunaListener(boolean copiarNomeColunaListener) {
		Preferencias.copiarNomeColunaListener = copiarNomeColunaListener;
	}

	public static boolean isFicharioComRolagem() {
		return ficharioComRolagem;
	}

	public static void setFicharioComRolagem(boolean ficharioComRolagem) {
		Preferencias.ficharioComRolagem = ficharioComRolagem;
	}

	public static boolean isAbrirAutoDestacado() {
		return abrirAutoDestacado;
	}

	public static void setAbrirAutoDestacado(boolean abrirAutoDestacado) {
		Preferencias.abrirAutoDestacado = abrirAutoDestacado;
	}

	public static boolean isFecharAposSoltar() {
		return fecharAposSoltar;
	}

	public static void setFecharAposSoltar(boolean fecharAposSoltar) {
		Preferencias.fecharAposSoltar = fecharAposSoltar;
	}

	public static int getPosicaoAbaFichario() {
		return posicaoAbaFichario;
	}

	public static void setPosicaoAbaFichario(int posicaoAbaFichario) {
		Preferencias.posicaoAbaFichario = posicaoAbaFichario;
	}

	public static boolean isAbrirAuto() {
		return abrirAuto;
	}

	public static void setAbrirAuto(boolean abrirAuto) {
		Preferencias.abrirAuto = abrirAuto;
	}

	public static int getIntervaloPesquisaAuto() {
		return intervaloPesquisaAuto;
	}

	public static void setIntervaloPesquisaAuto(int intervaloPesquisaAuto) {
		Preferencias.intervaloPesquisaAuto = intervaloPesquisaAuto;
	}
}