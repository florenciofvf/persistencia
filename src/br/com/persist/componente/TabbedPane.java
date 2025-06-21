package br.com.persist.componente;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Mensagens;

public class TabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	@Override
	public void addTab(String chaveRotulo, Icon icon, Component component) {
		super.addTab(Mensagens.getString(chaveRotulo), icon, component);
		borda();
	}

	@Override
	public void addTab(String chaveRotulo, Component component) {
		super.addTab(Mensagens.getString(chaveRotulo), component);
		borda();
	}

	public void borda() {
		setBorder(BorderFactory.createEmptyBorder());
	}
}