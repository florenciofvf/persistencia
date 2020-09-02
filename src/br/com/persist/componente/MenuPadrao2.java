package br.com.persist.componente;

import javax.swing.Icon;

public class MenuPadrao2 extends Menu {
	private static final long serialVersionUID = 1L;
	protected Action comAspasAcao = Action.actionMenuComAspas();
	protected Action semAspasAcao = Action.actionMenuSemAspas();

	public MenuPadrao2(String rotulo, Icon icon, String naoChave) {
		super(rotulo, icon, naoChave);
		addMenuItem(semAspasAcao);
		addMenuItem(comAspasAcao);
	}

	public MenuPadrao2(String chaveRotulo) {
		super(chaveRotulo, null);
		addMenuItem(semAspasAcao);
		addMenuItem(comAspasAcao);
	}
}