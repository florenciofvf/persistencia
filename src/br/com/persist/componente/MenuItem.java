package br.com.persist.componente;

import javax.swing.Action;
import javax.swing.JMenuItem;

import br.com.persist.assistencia.Icone;
import br.com.persist.assistencia.Mensagens;

public class MenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	private transient Object object;

	public MenuItem(String rotulo, boolean rotuloChave, Icone icone) {
		super(rotuloChave ? Mensagens.getString(rotulo) : rotulo, icone != null ? icone.getIcon() : null);
	}

	public MenuItem(String chaveRotulo, Icone icone) {
		this(chaveRotulo, true, icone);
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