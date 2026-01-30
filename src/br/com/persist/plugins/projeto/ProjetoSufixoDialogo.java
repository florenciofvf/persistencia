package br.com.persist.plugins.projeto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Panel;

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
		form.pack();
		form.setSize(form.getWidth(), Constantes.SIZE3.height);
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
		setSize(Constantes.SIZE3);
		toolbar.ini(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new PanelSufixos());
	}

	private class PanelSufixos extends Panel {
		private static final long serialVersionUID = 1L;

		public PanelSufixos() {
			super(new GridLayout(1, 0));
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
	}
}