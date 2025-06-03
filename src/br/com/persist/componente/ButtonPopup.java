package br.com.persist.componente;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import br.com.persist.assistencia.Mensagens;

public abstract class ButtonPopup extends Button {
	private static final long serialVersionUID = 1L;
	private Popup popup = new Popup();

	protected ButtonPopup(String chaveRotulo, Icon icon) {
		setToolTipText(Mensagens.getString(chaveRotulo));
		addActionListener(e -> popupShow());
		setComponentPopupMenu(popup);
		setIcon(icon);
	}

	protected void popupShow() {
		popupPreShow();
		popup.show(this, 5, 5);
	}

	protected void popupPreShow() {
	}

	protected void addMenuItem(Action action) {
		addMenuItem(false, action);
	}

	protected void addMenuItem(MenuItem item) {
		addMenuItem(false, item);
	}

	protected void addMenu(Menu menu) {
		addMenu(false, menu);
	}

	protected void addMenu(boolean separador, Menu menu) {
		if (separador) {
			addSeparator();
		}
		popup.add(menu);
	}

	protected void addMenuItem(boolean separador, Action action) {
		if (separador) {
			addSeparator();
		}
		popup.addMenuItem(action);
	}

	protected void addMenuItem(boolean separador, MenuItem item) {
		if (separador) {
			addSeparator();
		}
		popup.addMenuItem(item);
	}

	protected void addItem(JCheckBoxMenuItem item) {
		popup.add(item);
	}

	public void addSeparator() {
		popup.addSeparator();
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

	private Component getPrimeiroItem(Class<?> classe) {
		for (int i = 0; i < popup.getComponentCount(); i++) {
			Component c = popup.getComponent(i);
			if (c.getClass() == classe) {
				return c;
			}
		}
		return null;
	}

	public void excluirItens(Class<?> classe) {
		Component c = getPrimeiroItem(classe);
		while (c != null) {
			popup.remove(c);
			c = getPrimeiroItem(classe);
		}
	}

	public Component[] getComponentes() {
		return popup.getComponents();
	}

	public void limparPopup() {
		popup.limpar();
	}

	protected Action actionMenu(String chaveRotulo, Icon icone) {
		return Action.actionMenu(chaveRotulo, icone);
	}

	protected Action actionMenu(String chaveRotulo) {
		return actionMenu(chaveRotulo, null);
	}
}