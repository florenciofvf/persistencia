package br.com.persist.util;

import javax.swing.Icon;

public class ButtonPadrao1 extends ButtonPopup {
	private static final long serialVersionUID = 1L;
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action ficharioAcao = Action.actionMenuFichario();
	protected Action dialogoAcao = Action.actionMenuDialogo();

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