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

	protected MenuItem addMenuItem(Action action) {
		return addMenuItem(false, action);
	}

	protected MenuItem addMenuItem(boolean separador, Action action) {
		if (separador) {
			addSeparator();
		}
		MenuItem item = new MenuItem(action);
		add(item);
		return item;
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

	protected Action actionMenu(String chaveRotulo) {
		return actionMenu(chaveRotulo, null);
	}
}