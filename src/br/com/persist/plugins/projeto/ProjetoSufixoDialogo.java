package br.com.persist.plugins.projeto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;

public class ProjetoSufixoDialogo {
	private ProjetoSufixoDialogo() {
	}

	public static void view(Component c) {
		Component comp = Util.getViewParent(c);
		SufixoDialogo form = null;
		if (comp instanceof Frame) {
			form = new SufixoDialogo((Frame) comp);
		} else if (comp instanceof Dialog) {
			form = new SufixoDialogo((Dialog) comp);
		} else {
			form = new SufixoDialogo((Frame) null);
		}
		form.setSize(Constantes.SIZE2);
		form.setLocationRelativeTo(comp != null ? comp : c);
		form.setVisible(true);
	}
}

class SufixoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();

	SufixoDialogo(Frame frame) {
		super(frame, "Sufixos");
		init();
	}

	SufixoDialogo(Dialog dialog) {
		super(dialog, "Sufixos");
		init();
	}

	private void init() {
		toolbar.ini(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(new PanelSufixos()));
	}

	private class PanelSufixos extends Panel {
		private static final long serialVersionUID = 1L;

		public PanelSufixos() {
			super(new GridLayout(0, 1, 10, 10));
			for (ChaveIcone item : MapaSufixos.getLista()) {
				Label label = new Label(item.chave, false);
				label.setIcon(item.icone);
				add(label);
			}
			Label label = new Label("isFile", false);
			label.setIcon(Icones.TEXTO);
			add(label);
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
	}
}