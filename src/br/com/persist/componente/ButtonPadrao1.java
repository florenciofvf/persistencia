package br.com.persist.componente;

import javax.swing.Icon;

public class ButtonPadrao1 extends ButtonPopup {
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action ficharioAcao = Action.actionMenuFichario();
	protected Action dialogoAcao = Action.actionMenuDialogo();
	private static final long serialVersionUID = 1L;

	public ButtonPadrao1(String chaveRotulo, Icon icon) {
		this(chaveRotulo, icon, true);
	}

	public ButtonPadrao1(String chaveRotulo, Icon icon, boolean dialogo) {
		super(chaveRotulo, icon);
		addMenuItem(formularioAcao);
		addMenuItem(ficharioAcao);
		if (dialogo) {
			addMenuItem(dialogoAcao);
		}
	}
}