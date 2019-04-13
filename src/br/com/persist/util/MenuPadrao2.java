package br.com.persist.util;

import br.com.persist.comp.Menu;

public class MenuPadrao2 extends Menu {
	private static final long serialVersionUID = 1L;
	protected Action comAspasAcao = Action.actionMenuComAspas();
	protected Action semAspasAcao = Action.actionMenuSemAspas();

	public MenuPadrao2(String chaveRotulo) {
		super(chaveRotulo, null);
		addMenuItem(semAspasAcao);
		addMenuItem(comAspasAcao);
	}
}