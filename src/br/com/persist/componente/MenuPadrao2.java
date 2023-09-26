package br.com.persist.componente;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

public class MenuPadrao2 extends Menu {
	protected Action comAspasAcao = Action.actionMenuComAspas();
	protected Action semAspasAcao = Action.actionMenuSemAspas();
	private static final long serialVersionUID = 1L;

	public MenuPadrao2(String rotulo, boolean chaveRotulo, Icon icon) {
		super(rotulo, chaveRotulo, icon);
		addMenuItem(semAspasAcao);
		addMenuItem(comAspasAcao);
	}

	public MenuPadrao2(String chaveRotulo) {
		this(chaveRotulo, true, null);
	}

	public void habilitar(boolean b) {
		semAspasAcao.setEnabled(b);
		comAspasAcao.setEnabled(b);
	}

	protected void addItem(JCheckBoxMenuItem item) {
		add(item);
	}
}