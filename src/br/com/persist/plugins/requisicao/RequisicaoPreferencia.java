package br.com.persist.plugins.requisicao;

import java.util.prefs.Preferences;

import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class RequisicaoPreferencia {
	private static String binarios;

	private RequisicaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		binarios = pref.get("requisicoes_binarios", "application/dpf");
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.put("requisicoes_binarios", binarios);
	}

	public static String getBinarios() {
		if (binarios == null) {
			binarios = "";
		}
		return binarios;
	}

	public static void setBinarios(String binarios) {
		RequisicaoPreferencia.binarios = binarios;
	}

	public static boolean ehBinario(String string) {
		if (Util.estaVazio(string)) {
			return false;
		}
		string = string.trim();
		String[] array = getBinarios().split(",");
		if (array != null) {
			for (String s : array) {
				if (!Util.estaVazio(s) && s.trim().equalsIgnoreCase(string)) {
					return true;
				}
			}
		}
		return false;
	}
}