package br.com.persist.componente;

import java.awt.Component;

import javax.swing.Icon;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Preferencias;

public class MenuPadrao3 extends Menu {
	private static final long serialVersionUID = 1L;
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action dialogoAcao = Action.actionMenuDialogo();

	public MenuPadrao3(String rotulo, Icon icon, String naoChave) {
		super(rotulo, icon, naoChave);

		String[] strings = Preferencias.getFormDialogo().split(",");

		for (String s : strings) {
			if (Constantes.FORM.equals(s)) {
				addMenuItem(formularioAcao);
			} else if (Constantes.DIALOG.equals(s)) {
				addMenuItem(dialogoAcao);
			}
		}
	}

	public MenuPadrao3(String chaveRotulo, Icon icon) {
		super(chaveRotulo, icon);

		String[] strings = Preferencias.getFormDialogo().split(",");

		for (String s : strings) {
			if (Constantes.FORM.equals(s)) {
				addMenuItem(formularioAcao);
			} else if (Constantes.DIALOG.equals(s)) {
				addMenuItem(dialogoAcao);
			}
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