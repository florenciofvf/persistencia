package br.com.persist.comp;

import javax.swing.JPopupMenu;
import br.com.persist.util.Action;

public class Popup extends JPopupMenu {
	private static final long serialVersionUID = 1L;

	public void addMenuItem(Action action) {
		add(new MenuItem(action));
	}

	public void addMenuItem(MenuItem item) {
		add(item);
	}
}