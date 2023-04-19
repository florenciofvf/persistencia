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

	public MenuPadrao1(String rotulo, boolean chaveRotulo, Icon icon, boolean dialogo) {
		super(rotulo, chaveRotulo, icon);
		init(dialogo);
	}

	public MenuPadrao1(String chaveRotulo, Icon icon, boolean dialogo) {
		this(chaveRotulo, true, icon, dialogo);
	}

	public MenuPadrao1(String chaveRotulo, Icon icon) {
		this(chaveRotulo, icon, true);
	}

	private void init(boolean dialogo) {
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