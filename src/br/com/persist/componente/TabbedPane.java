package br.com.persist.componente;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Icone;
import br.com.persist.assistencia.Mensagens;

public class TabbedPane extends JTabbedPane {
	public static final int SCROLL = JTabbedPane.SCROLL_TAB_LAYOUT;
	public static final int WRAP = JTabbedPane.WRAP_TAB_LAYOUT;
	private static final long serialVersionUID = 1L;
	private final boolean chaveProperty;

	public TabbedPane(boolean chaveProperty) {
		this.chaveProperty = chaveProperty;
		borda();
	}

	public void addTab(String title, Icone icone, Component component) {
		if (chaveProperty) {
			super.addTab(Mensagens.getString(title), icone != null ? icone.getIcon() : null, component);
		} else {
			super.addTab(title, icone != null ? icone.getIcon() : null, component);
		}
	}

	@Override
	public void addTab(String title, Component component) {
		if (chaveProperty) {
			super.addTab(Mensagens.getString(title), component);
		} else {
			super.addTab(title, component);
		}
	}

	public void borda() {
		setBorder(BorderFactory.createEmptyBorder());
	}
}