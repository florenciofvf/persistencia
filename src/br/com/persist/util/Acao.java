package br.com.persist.util;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public abstract class Acao extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public Acao(boolean menu, String chaveRotulo, Icon icone) {
		putValue(menu ? Action.NAME : Action.SHORT_DESCRIPTION, Mensagens.getString(chaveRotulo));
		putValue(Action.SMALL_ICON, icone);
	}
}