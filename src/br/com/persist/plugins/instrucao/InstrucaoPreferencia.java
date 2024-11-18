package br.com.persist.plugins.instrucao;

import java.awt.Font;
import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class InstrucaoPreferencia {
	private static final String DESENHAR_ESPACO_RETORNO = "desenhar_espaco_retorno";
	private static final String INSTRUCAO_FONT_SIZE = "instrucao_font_size";
	private static boolean desenharEspacoRetorno;
	private static boolean exibirArqIgnorados;
	private static int instrucaoFonteSize;

	private InstrucaoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		exibirArqIgnorados = pref.getBoolean("instrucao_exibir_arq_ignorados", false);
		desenharEspacoRetorno = pref.getBoolean(DESENHAR_ESPACO_RETORNO, false);
		instrucaoFonteSize = pref.getInt(INSTRUCAO_FONT_SIZE, 12);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("instrucao_exibir_arq_ignorados", exibirArqIgnorados);
		pref.putBoolean(DESENHAR_ESPACO_RETORNO, desenharEspacoRetorno);
		pref.putInt(INSTRUCAO_FONT_SIZE, instrucaoFonteSize);
	}

	public static int getInstrucaoFonteSize() {
		return instrucaoFonteSize;
	}

	public static void setInstrucaoFonteSize(int instrucaoFonteSize) {
		InstrucaoPreferencia.instrucaoFonteSize = instrucaoFonteSize;
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
		pref.putInt(INSTRUCAO_FONT_SIZE, font.getSize());
		pref.put("instrucao_font_name", font.getName());
	}

	public static Font getFontPreferencia() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		String name = pref.get("instrucao_font_name", null);
		if (name == null) {
			return null;
		}
		return new Font(name, pref.getInt("instrucao_font_style", 0), pref.getInt(INSTRUCAO_FONT_SIZE, 12));
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