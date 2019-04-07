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
		return Action.actionIcon(Constantes.LABEL_ATUALIZAR, Icones.ATUALIZAR);
	}

	public static Action actionMenuAtualizar() {
		return Action.actionMenu(Constantes.LABEL_ATUALIZAR, Icones.ATUALIZAR);
	}

	public static Action actionIconUpdate() {
		return Action.actionIcon(Constantes.LABEL_ATUALIZAR, Icones.UPDATE);
	}

	public static Action actionIconBaixar() {
		return Action.actionIcon("label.baixar", Icones.BAIXAR);
	}

	public static Action actionIconSalvar() {
		return Action.actionIcon("label.salvar", Icones.SALVAR);
	}

	public static Action actionIconDestacar() {
		return Action.actionIcon(Constantes.LABEL_DESTACAR, Icones.ARRASTAR, e -> {
		});
	}

	public static Action actionMenuFormulario() {
		return new Action(true, Constantes.LABEL_FORMULARIO, null);// Icones.PANEL
	}

	public static Action actionMenuFichario() {
		return new Action(true, Constantes.LABEL_FICHARIO, null);
	}

	public static Action actionMenuDesktop() {
		return new Action(true, Constantes.LABEL_DESKTOP, null);// Icones.PANEL2
	}

	public static Action actionMenuDialogo() {
		return new Action(true, Constantes.LABEL_DIALOGO, null);
	}
}