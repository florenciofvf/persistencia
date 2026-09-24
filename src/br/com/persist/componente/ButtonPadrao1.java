package br.com.persist.componente;

import br.com.persist.assistencia.Icone;

public class ButtonPadrao1 extends ButtonPopup {
	protected Action formularioAcao = Action.actionMenuFormulario();
	protected Action ficharioAcao = Action.actionMenuFichario();
	protected Action dialogoAcao = Action.actionMenuDialogo();
	private static final long serialVersionUID = 1L;

	public ButtonPadrao1(String chaveRotulo, Icone icone) {
		this(chaveRotulo, icone, true);
	}

	public ButtonPadrao1(String chaveRotulo, Icone icone, boolean dialogo) {
		super(chaveRotulo, icone);
		addMenuItem(formularioAcao);
		addMenuItem(ficharioAcao);
		if (dialogo) {
			addMenuItem(dialogoAcao);
		}
	}
}