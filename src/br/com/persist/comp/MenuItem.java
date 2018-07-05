package br.com.persist.comp;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import br.com.persist.util.Mensagens;

public class MenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;

	public MenuItem(Action action) {
		super(action);
	}

	public MenuItem(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}

	public MenuItem(String chaveRotulo, Icon icon) {
		super(Mensagens.getString(chaveRotulo), icon);
	}
}