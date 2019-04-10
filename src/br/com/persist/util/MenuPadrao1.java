package br.com.persist.util;

import java.awt.Component;

import javax.swing.Icon;

import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;

public class MenuPadrao1 extends Menu {
	private static final long serialVersionUID = 1L;
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action ficharioAcao = Action.actionMenuFichario();
	protected Action dialogoAcao = Action.actionMenuDialogo();

	public MenuPadrao1(String chaveRotulo, Icon icon) {
		this(chaveRotulo, icon, true);
	}

	public MenuPadrao1(String chaveRotulo, Icon icon, boolean dialogo) {
		super(chaveRotulo, icon);

		addMenuItem(formularioAcao);
		addMenuItem(ficharioAcao);

		if (dialogo) {
			addMenuItem(dialogoAcao);
		}
	}

	public void excluirAcao(Action action) {
		for (int i = 0; i < getComponentCount(); i++) {
			Component c = getComponent(i);

			if (c instanceof MenuItem) {
				MenuItem item = (MenuItem) c;

				if (item.getAction() == action) {
					remove(item);
				}
			}
		}
	}
}