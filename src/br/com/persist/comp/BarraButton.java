package br.com.persist.comp;

import javax.swing.JToolBar;

import br.com.persist.util.Action;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;

public class BarraButton extends JToolBar {
	private static final long serialVersionUID = 1L;

	protected void ini(IJanela janela) {
		if (janela != null) {
			Action fecharAcao = Action.actionIcon("label.fechar", Icones.SAIR);
			fecharAcao.setActionListener(e -> janela.fechar());
			add(new Button(fecharAcao));
			addSeparator();
		}
	}

	protected void addButton(Action action) {
		add(new Button(action));
	}
}