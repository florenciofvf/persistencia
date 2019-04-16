package br.com.persist.util;

import java.awt.Component;

import javax.swing.Icon;

import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;

public class MenuPadrao3 extends Menu {
	private static final long serialVersionUID = 1L;
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action dialogoAcao = Action.actionMenuDialogo();

	public MenuPadrao3(String rotulo, Icon icon, String naoChave) {
		super(rotulo, icon, naoChave);

		addMenuItem(formularioAcao);
		addMenuItem(dialogoAcao);
	}

	public MenuPadrao3(String chaveRotulo, Icon icon) {
		super(chaveRotulo, icon);

		addMenuItem(formularioAcao);
		addMenuItem(dialogoAcao);
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