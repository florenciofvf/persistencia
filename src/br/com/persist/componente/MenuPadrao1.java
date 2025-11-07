package br.com.persist.componente;

import java.awt.Component;

import javax.swing.Icon;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Preferencias;

public class MenuPadrao1 extends Menu {
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action ficharioAcao = Action.actionMenuFichario();
	protected Action dialogoAcao = Action.actionMenuDialogo();
	private static final long serialVersionUID = 1L;

	public MenuPadrao1(String rotulo, boolean chaveRotulo, Icon icon) {
		super(rotulo, chaveRotulo, icon);
		init();
	}

	public MenuPadrao1(String chaveRotulo, Icon icon) {
		this(chaveRotulo, true, icon);
	}

	private void init() {
		String[] strings = Preferencias.getFormFichaDialogo().split(",");
		for (String s : strings) {
			if (Constantes.FORM.equals(s)) {
				addMenuItem(formularioAcao);
			} else if (Constantes.FICHA.equals(s)) {
				addMenuItem(ficharioAcao);
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