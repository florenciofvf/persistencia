package br.com.persist.plugins.objeto;

import java.awt.Color;
import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class ObjetoPreferencia {
	private static boolean habilitadoInnerJoinsObjeto;
	private static Color corAntesTotalRecente;
	private static Color corTotalAtual;
	private static Color corComparaRec;

	private ObjetoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		habilitadoInnerJoinsObjeto = pref.getBoolean("habilitado_inner_joins_objeto", false);
		corAntesTotalRecente = new Color(pref.getInt("cor_antes_total_recente", Color.BLACK.getRGB()));
		corTotalAtual = new Color(pref.getInt("cor_total_atual", Color.ORANGE.getRGB()));
		corComparaRec = new Color(pref.getInt("cor_compara_rec", Color.CYAN.getRGB()));
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("habilitado_inner_joins_objeto", habilitadoInnerJoinsObjeto);
		pref.putInt("cor_antes_total_recente", corAntesTotalRecente.getRGB());
		pref.putInt("cor_total_atual", corTotalAtual.getRGB());
		pref.putInt("cor_compara_rec", corComparaRec.getRGB());
	}

	public static boolean isHabilitadoInnerJoinsObjeto() {
		return habilitadoInnerJoinsObjeto;
	}

	public static void setHabilitadoInnerJoinsObjeto(boolean habilitadoInnerJoinsObjeto) {
		ObjetoPreferencia.habilitadoInnerJoinsObjeto = habilitadoInnerJoinsObjeto;
	}

	public static Color getCorAntesTotalRecente() {
		return corAntesTotalRecente;
	}

	public static void setCorAntesTotalRecente(Color corAntesTotalRecente) {
		ObjetoPreferencia.corAntesTotalRecente = corAntesTotalRecente;
	}

	public static Color getCorTotalAtual() {
		return corTotalAtual;
	}

	public static void setCorTotalAtual(Color corTotalAtual) {
		ObjetoPreferencia.corTotalAtual = corTotalAtual;
	}

	public static Color getCorComparaRec() {
		return corComparaRec;
	}

	public static void setCorComparaRec(Color corComparaRec) {
		ObjetoPreferencia.corComparaRec = corComparaRec;
	}
}