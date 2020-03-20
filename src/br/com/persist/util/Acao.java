package br.com.persist.util;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public abstract class Acao extends AbstractAction {
	private static final long serialVersionUID = 1L;
	protected final String chave;
	protected final boolean menu;

	public Acao(boolean menu, String chaveRotulo, Icon icone) {
		putValue(Action.SMALL_ICON, icone);
		setRotulo(menu, chaveRotulo);
		this.chave = chaveRotulo;
		this.menu = menu;
	}

	public String getChave() {
		return chave;
	}

	public void setRotulo(boolean menu, String chaveRotulo) {
		putValue(menu ? Action.NAME : Action.SHORT_DESCRIPTION, Mensagens.getString(chaveRotulo));
	}

	public void rotulo(String chaveRotulo) {
		setRotulo(menu, chaveRotulo);
	}
}