package br.com.persist.componente;

import br.com.persist.assistencia.Icone;

public class MenuPadrao2 extends Menu {
	protected Action comAspasAcao = Action.actionMenuComAspas();
	protected Action semAspasAcao = Action.actionMenuSemAspas();
	private static final long serialVersionUID = 1L;

	public MenuPadrao2(String rotulo, boolean chaveRotulo, Icone icone) {
		super(rotulo, chaveRotulo, icone);
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

	protected void addItem(CheckBoxMenuItem item) {
		add(item);
	}
}