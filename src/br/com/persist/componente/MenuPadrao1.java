package br.com.persist.componente;

import java.awt.Component;

import javax.swing.Icon;

import br.com.persist.util.Constantes;
import br.com.persist.util.Preferencias;

public class MenuPadrao1 extends Menu {
	private static final long serialVersionUID = 1L;
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action ficharioAcao = Action.actionMenuFichario();
	protected Action dialogoAcao = Action.actionMenuDialogo();

	public MenuPadrao1(String chaveRotulo, Icon icon, boolean dialogo) {
		super(chaveRotulo, icon);

		String[] strings = Preferencias.getFormFichaDialogo().split(",");

		if (dialogo) {
			for (String s : strings) {
				if (Constantes.FORM.equals(s)) {
					addMenuItem(formularioAcao);
				} else if (Constantes.FICHA.equals(s)) {
					addMenuItem(ficharioAcao);
				} else if (Constantes.DIALOG.equals(s)) {
					addMenuItem(dialogoAcao);
				}
			}
		} else {
			for (String s : strings) {
				if (Constantes.FORM.equals(s)) {
					addMenuItem(formularioAcao);
				} else if (Constantes.FICHA.equals(s)) {
					addMenuItem(ficharioAcao);
				}
			}
		}
	}

	public MenuPadrao1(String chaveRotulo, Icon icon) {
		this(chaveRotulo, icon, true);
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