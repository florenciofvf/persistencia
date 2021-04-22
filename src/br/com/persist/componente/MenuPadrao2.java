package br.com.persist.componente;

import javax.swing.Icon;

public class MenuPadrao2 extends Menu {
	private static final long serialVersionUID = 1L;
	protected Action comAspasAcao = Action.actionMenuComAspas();
	protected Action semAspasAcao = Action.actionMenuSemAspas();

	public MenuPadrao2(String rotulo, boolean chaveRotulo, Icon icon) {
		super(rotulo, chaveRotulo, icon);
		addMenuItem(semAspasAcao);
		addMenuItem(comAspasAcao);
	}

	public MenuPadrao2(String chaveRotulo) {
		this(chaveRotulo, true, null);
	}
}