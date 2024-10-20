package br.com.persist.componente;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Popup extends JPopupMenu {
	private static final long serialVersionUID = 1L;

	public JMenuItem add(boolean separador, JMenuItem menuItem) {
		if (separador) {
			addSeparator();
		}
		return add(menuItem);
	}

	public MenuItem addMenuItem(boolean separador, Action action, String hint) {
		MenuItem item = addMenuItem(separador, action);
		item.setToolTipText(hint);
		return item;
	}

	public MenuItem addMenuItem(boolean separador, Action action) {
		return addMenuItem(separador, new MenuItem(action));
	}

	public MenuItem addMenuItem(boolean separador, MenuItem item) {
		if (separador) {
			addSeparator();
		}
		add(item);
		return item;
	}

	public MenuItem addMenuItem(Action action, String hint) {
		MenuItem item = addMenuItem(action);
		item.setToolTipText(hint);
		return item;
	}

	public MenuItem addMenuItem(Action action) {
		return addMenuItem(false, action);
	}

	public MenuItem addMenuItem(MenuItem item) {
		return addMenuItem(false, item);
	}

	public void limpar() {
		while (getComponentCount() > 0) {
			remove(0);
		}
	}

	protected Action actionMenu(String chaveRotulo, Icon icone) {
		return Action.actionMenu(chaveRotulo, icone);
	}

	protected Action actionMenu(String chaveRotulo) {
		return actionMenu(chaveRotulo, null);
	}
}