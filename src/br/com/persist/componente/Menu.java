package br.com.persist.componente;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.assistencia.Mensagens;

public class Menu extends JMenu {
	private static final long serialVersionUID = 1L;

	public Menu(String rotulo, boolean chaveRotulo, Icon icon) {
		super(chaveRotulo ? Mensagens.getString(rotulo) : rotulo);
		setIcon(icon);
	}

	public Menu(String chaveRotulo, Icon icon) {
		this(chaveRotulo, true, icon);
	}

	public Menu(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}

	protected void addMenuItem(Action action) {
		addMenuItem(false, action);
	}

	protected void addMenuItem(boolean separador, Action action) {
		if (separador) {
			addSeparator();
		}
		add(new MenuItem(action));
	}

	public void add(boolean separador, JMenuItem menuItem) {
		if (separador) {
			addSeparator();
		}
		add(menuItem);
	}

	protected Action actionMenu(String chaveRotulo, Icon icone) {
		return Action.actionMenu(chaveRotulo, icone);
	}
}