package br.com.persist.comp;

import javax.swing.JPopupMenu;
import br.com.persist.util.Action;

public class Popup extends JPopupMenu {
	private static final long serialVersionUID = 1L;

	public void addMenuItem(Action action) {
		addMenuItem(false, action);
	}

	public void addMenuItem(boolean separador, Action action) {
		if (separador) {
			addSeparator();
		}

		add(new MenuItem(action));
	}

	public void addMenuItem(MenuItem item) {
		addMenuItem(false, item);
	}

	public void addMenuItem(boolean separador, MenuItem item) {
		if (separador) {
			addSeparator();
		}

		add(item);
	}
}