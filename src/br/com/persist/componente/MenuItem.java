package br.com.persist.componente;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import br.com.persist.util.Mensagens;

public class MenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	private transient Object object;

	public MenuItem(String chaveRotulo, Icon icon) {
		super(Mensagens.getString(chaveRotulo), icon);
	}

	public MenuItem(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}

	public MenuItem(Action action) {
		super(action);
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}