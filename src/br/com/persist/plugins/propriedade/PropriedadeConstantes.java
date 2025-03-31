package br.com.persist.plugins.propriedade;

import java.awt.GraphicsEnvironment;

public class PropriedadeConstantes {
	protected static final String[] FONTES = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames();
	public static final String LABEL_PROPRIEDADE_MIN = "label.propriedade_min";
	public static final String PAINEL_PROPRIEDADE = "PAINEL PROPRIEDADE";
	public static final String LABEL_PROPRIEDADE = "label.propriedade";
	public static final String PROPRIEDADES = "propriedades";
	public static final String PROPRIEDADE = "propriedade";
	public static final String IGNORADOS = "ignorados";
	public static final String TABULAR = "        ";
	public static final String TAB = "\t";
	public static final String TAB2 = TAB + TAB;
	public static final String TAB3 = TAB2 + TAB;

	private PropriedadeConstantes() {
	}
}