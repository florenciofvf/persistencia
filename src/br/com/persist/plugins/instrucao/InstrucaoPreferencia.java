package br.com.persist.plugins.instrucao;

import java.awt.Font;
import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class InstrucaoPreferencia {
	private static final String DESENHAR_ESPACO_RETORNO = "desenhar_espaco_retorno";
	private static boolean desenharEspacoRetorno;
	private static boolean exibirArqIgnorados;

	private InstrucaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		exibirArqIgnorados = pref.getBoolean("instrucao_exibir_arq_ignorados", false);
		desenharEspacoRetorno = pref.getBoolean(DESENHAR_ESPACO_RETORNO, false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("instrucao_exibir_arq_ignorados", exibirArqIgnorados);
		pref.putBoolean(DESENHAR_ESPACO_RETORNO, desenharEspacoRetorno);
	}

	public static boolean isExibirArqIgnorados() {
		return exibirArqIgnorados;
	}

	public static void setExibirArqIgnorados(boolean exibirArqIgnorados) {
		InstrucaoPreferencia.exibirArqIgnorados = exibirArqIgnorados;
	}

	public static void setFontPreferencia(Font font) {
		if (font == null) {
			return;
		}
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt("instrucao_font_style", font.getStyle());
		pref.putInt("instrucao_font_size", font.getSize());
		pref.put("instrucao_font_name", font.getName());
	}

	public static Font getFontPreferencia() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		String name = pref.get("instrucao_font_name", null);
		if (name == null) {
			return null;
		}
		return new Font(name, pref.getInt("instrucao_font_style", 0), pref.getInt("instrucao_font_size", 0));
	}

	public static boolean isDesenharEspacoRetorno() {
		return desenharEspacoRetorno;
	}

	public static void setDesenharEspacoRetorno(boolean desenharEspacoRetorno) {
		InstrucaoPreferencia.desenharEspacoRetorno = desenharEspacoRetorno;
	}

	public static void setPaintER(boolean paintER) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean(DESENHAR_ESPACO_RETORNO, paintER);
	}

	public static boolean getPaintER() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		return pref.getBoolean(DESENHAR_ESPACO_RETORNO, false);
	}
}