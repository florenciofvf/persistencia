package br.com.persist.util;

import java.awt.Component;

import javax.swing.Icon;

import br.com.persist.comp.Button;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;

public class ButtonPadrao1 extends Button {
	private static final long serialVersionUID = 1L;
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action ficharioAcao = Action.actionMenuFichario();
	protected Action dialogoAcao = Action.actionMenuDialogo();
	protected Popup popup = new Popup();

	public ButtonPadrao1(String chaveRotulo, Icon icon) {
		this(chaveRotulo, icon, true);
	}

	public ButtonPadrao1(String chaveRotulo, Icon icon, boolean dialogo) {
		setToolTipText(Mensagens.getString(chaveRotulo));
		addActionListener(e -> popup.show(this, 5, 5));
		popup.addMenuItem(formularioAcao);
		popup.addMenuItem(ficharioAcao);
		if (dialogo) {
			popup.addMenuItem(dialogoAcao);
		}
		setComponentPopupMenu(popup);
		setIcon(icon);
	}

	protected void addMenuItem(Action action) {
		popup.addMenuItem(action);
	}

	public void excluirAcao(Action action) {
		for (int i = 0; i < popup.getComponentCount(); i++) {
			Component c = popup.getComponent(i);

			if (c instanceof MenuItem) {
				MenuItem item = (MenuItem) c;

				if (item.getAction() == action) {
					popup.remove(item);
				}
			}
		}
	}
}