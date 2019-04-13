package br.com.persist.util;

import java.awt.Component;

import javax.swing.Icon;

import br.com.persist.comp.Button;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;

public class ButtonPopup extends Button {
	private static final long serialVersionUID = 1L;
	protected Popup popup = new Popup();

	public ButtonPopup(String chaveRotulo, Icon icon) {
		setToolTipText(Mensagens.getString(chaveRotulo));
		addActionListener(e -> popup.show(this, 5, 5));
		setComponentPopupMenu(popup);
		setIcon(icon);
	}

	protected void addMenuItem(Action action) {
		addMenuItem(false, action);
	}

	protected void addMenuItem(MenuItem item) {
		addMenuItem(false, item);
	}

	protected void addMenuItem(boolean separador, Action action) {
		if (separador) {
			popup.addSeparator();
		}

		popup.addMenuItem(action);
	}

	protected void addMenuItem(boolean separador, MenuItem item) {
		if (separador) {
			popup.addSeparator();
		}

		popup.addMenuItem(item);
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