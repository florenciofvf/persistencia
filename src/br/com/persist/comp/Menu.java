package br.com.persist.comp;

import javax.swing.Icon;
import javax.swing.JMenu;

import br.com.persist.util.Action;
import br.com.persist.util.Mensagens;

public class Menu extends JMenu {
	private static final long serialVersionUID = 1L;

	public Menu(String chaveRotulo, Icon icon) {
		super(Mensagens.getString(chaveRotulo));
		setIcon(icon);
	}

	public Menu(String rotulo, Icon icon, String naoChave) {
		super(rotulo);
		setIcon(icon);
	}

	public Menu(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}

	protected void addMenuItem(Action action) {
		add(new MenuItem(action));
	}
}