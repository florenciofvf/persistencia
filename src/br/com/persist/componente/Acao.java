package br.com.persist.componente;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import br.com.persist.assistencia.Mensagens;

public abstract class Acao extends AbstractAction {
	private static final long serialVersionUID = 1L;
	protected final String chave;
	protected final boolean menu;

	protected Acao(boolean menu, String rotulo, boolean chaveRotulo, Icon icone) {
		setRotulo(menu, rotulo, chaveRotulo);
		putValue(Action.SMALL_ICON, icone);
		this.chave = rotulo;
		this.menu = menu;
	}

	public String getChave() {
		return chave;
	}

	public void setRotulo(boolean menu, String rotulo, boolean chaveRotulo) {
		putValue(menu ? Action.NAME : Action.SHORT_DESCRIPTION, chaveRotulo ? Mensagens.getString(rotulo) : rotulo);
	}

	public String getText() {
		return (String) getValue(menu ? Action.NAME : Action.SHORT_DESCRIPTION);
	}

	public void setRotulo(boolean menu, String chaveRotulo) {
		setRotulo(menu, chaveRotulo, true);
	}

	public void rotulo(String chaveRotulo) {
		setRotulo(menu, chaveRotulo);
	}

	public void text(String rotulo) {
		setRotulo(menu, rotulo, false);
	}

	public void hint(String string) {
		putValue(Action.SHORT_DESCRIPTION, string);
	}
}