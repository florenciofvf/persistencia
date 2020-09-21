package br.com.persist.componente;

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

	public void addMenuItem(boolean separador, Action action) {
		addMenuItem(separador, new MenuItem(action));
	}

	public void addMenuItem(boolean separador, MenuItem item) {
		if (separador) {
			addSeparator();
		}
		add(item);
	}

	public void addMenuItem(Action action) {
		addMenuItem(false, action);
	}

	public void addMenuItem(MenuItem item) {
		addMenuItem(false, item);
	}
}