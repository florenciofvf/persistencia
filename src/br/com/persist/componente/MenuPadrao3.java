package br.com.persist.componente;

import java.awt.Component;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icone;
import br.com.persist.assistencia.Preferencias;

public class MenuPadrao3 extends Menu {
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action dialogoAcao = Action.actionMenuDialogo();
	private static final long serialVersionUID = 1L;

	public MenuPadrao3(String rotulo, boolean chaveRotulo, Icone icone) {
		super(rotulo, chaveRotulo, icone);
		init();
	}

	public MenuPadrao3(String chaveRotulo, Icone icone) {
		this(chaveRotulo, true, icone);
	}

	private void init() {
		String[] strings = Preferencias.getFormDialogo().split(",");
		for (String item : strings) {
			if (Constantes.FORM.equals(item)) {
				addMenuItem(formularioAcao);
			} else if (Constantes.DIALOG.equals(item)) {
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