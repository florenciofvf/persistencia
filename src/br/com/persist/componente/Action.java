package br.com.persist.componente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;

public class Action extends Acao {
	protected transient ActionListener actionListener;
	private static final long serialVersionUID = 1L;

	public Action(boolean menu, String rotulo, boolean chaveRotulo, Icon icone) {
		super(menu, rotulo, chaveRotulo, icone);
	}

	public Action(boolean menu, String chaveRotulo, Icon icone) {
		this(menu, chaveRotulo, true, icone);
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
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

	public static Action acaoIcon(String rotulo, Icon icone) {
		return new Action(false, rotulo, false, icone);
	}

	public static Action actionMenu(String chaveRotulo, Icon icone) {
		return new Action(true, chaveRotulo, icone);
	}

	public static Action acaoMenu(String rotulo, Icon icone) {
		return new Action(true, rotulo, false, icone);
	}

	public static Action actionIconAtualizar() {
		return Action.actionIcon(Constantes.LABEL_ATUALIZAR, Icones.ATUALIZAR);
	}

	public static Action actionIconLimpar() {
		return Action.actionIcon(Constantes.LABEL_LIMPAR, Icones.NOVO);
	}

	public static Action actionMenuAtualizar() {
		return Action.actionMenu(Constantes.LABEL_ATUALIZAR, Icones.ATUALIZAR);
	}

	public static Action actionMenuExcluir() {
		return Action.actionMenu("label.excluir", Icones.EXCLUIR);
	}

	public static Action actionIconExcluir() {
		return Action.actionIcon("label.excluir", Icones.EXCLUIR);
	}

	public static Action actionMenuClonar() {
		return Action.actionMenu("label.clonar", Icones.CLONAR);
	}

	public static Action actionMenuCopiar() {
		return Action.actionMenu("label.copiar", Icones.COPIA);
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

	public static Action actionIconNovo() {
		return Action.actionIcon(Constantes.LABEL_NOVO, Icones.NOVO);
	}

	public static Action actionIconDestacar() {
		return Action.actionIcon(Constantes.LABEL_ARRASTAR, Icones.ARRASTAR2, e -> {
		});
	}

	public static Action actionMenuFormulario() {
		return new Action(true, Constantes.LABEL_FORMULARIO, null);
	}

	public static Action actionMenuFichario() {
		return new Action(true, Constantes.LABEL_FICHARIO, null);
	}

	public static Action actionMenuDesktop() {
		return new Action(true, Constantes.LABEL_DESKTOP, null);
	}

	public static Action actionMenuDialogo() {
		return new Action(true, Constantes.LABEL_DIALOGO, null);
	}

	public static Action actionMenuComAspas() {
		return new Action(true, Constantes.LABEL_COM_ASPAS, Icones.ASPAS);
	}

	public static Action actionMenuSemAspas() {
		return new Action(true, Constantes.LABEL_SEM_ASPAS, null);
	}

	public static Action actionMenuFechar() {
		return Action.actionMenu(Constantes.LABEL_FECHAR, Icones.SAIR);
	}
}