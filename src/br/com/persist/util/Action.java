package br.com.persist.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;

public class Action extends Acao {
	private static final long serialVersionUID = 1L;
	protected transient ActionListener actionListener;

	private Action(boolean menu, String chaveRotulo, Icon icone) {
		super(menu, chaveRotulo, icone);
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (actionListener == null) {
			throw new IllegalStateException();
		}

		actionListener.actionPerformed(e);
	}

	public static Action actionIcon(String chaveRotulo, Icon icone, ActionListener actionListener) {
		Action action = new Action(false, chaveRotulo, icone);
		action.actionListener = actionListener;

		return action;
	}

	public static Action actionMenu(String chaveRotulo, Icon icone, ActionListener actionListener) {
		Action action = new Action(true, chaveRotulo, icone);
		action.actionListener = actionListener;

		return action;
	}

	public static Action actionIcon(String chaveRotulo, Icon icone) {
		return new Action(false, chaveRotulo, icone);
	}

	public static Action actionMenu(String chaveRotulo, Icon icone) {
		return new Action(true, chaveRotulo, icone);
	}

	public static Action actionIconAtualizar() {
		return Action.actionIcon("label.atualizar", Icones.ATUALIZAR);
	}

	public static Action actionIconBaixar() {
		return Action.actionIcon("label.baixar", Icones.BAIXAR);
	}

	public static Action actionIconSalvar() {
		return Action.actionIcon("label.salvar", Icones.SALVAR);
	}
}