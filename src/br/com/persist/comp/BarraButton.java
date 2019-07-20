package br.com.persist.comp;

import java.awt.Component;

import javax.swing.JToolBar;

import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;

public class BarraButton extends JToolBar {
	private static final long serialVersionUID = 1L;
	protected transient IJanela janela;

	public void ini(IJanela janela) {
		this.janela = janela;

		if (janela != null) {
			Action fecharAcao = Action.actionIcon(Constantes.LABEL_FECHAR, Icones.SAIR);
			fecharAcao.setActionListener(e -> janela.fechar());
			addButton(fecharAcao);
			addSeparator();
		}
	}

	protected void addButton(boolean separador, Action action) {
		if (separador) {
			addSeparator();
		}

		add(new Button(action));
	}

	protected void addButton(Action action) {
		addButton(false, action);
	}

	protected Component add(boolean separador, Component comp) {
		if (separador) {
			addSeparator();
		}

		return add(comp);
	}

	public void fechar() {
		if (janela != null) {
			janela.fechar();
		}
	}
}