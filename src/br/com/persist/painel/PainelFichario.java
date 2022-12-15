package br.com.persist.painel;

import javax.swing.JTabbedPane;

public class PainelFichario extends JTabbedPane {
	private static final long serialVersionUID = 7100686282883066124L;

	public int getTotalAbas() {
		return getTabCount();
	}
}