package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextArea;
import br.com.persist.comp.TextField;
import br.com.persist.util.IJanela;

public class ComparacaoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final BarraButton toolbar = new BarraButton();
	private final TextArea textArea = new TextArea();

	public ComparacaoContainer(IJanela janela) {
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, new Controle());
		panel.add(BorderLayout.CENTER, textArea);
		add(BorderLayout.CENTER, panel);
		add(BorderLayout.NORTH, toolbar);
	}

	private class Controle extends Panel {
		private static final long serialVersionUID = 1L;
		private Button btnArquivo1 = new Button("label.procurar");
		private Button btnArquivo2 = new Button("label.procurar");
		private Label lblArquivo1 = new Label("label.arquivo1");
		private Label lblArquivo2 = new Label("label.arquivo2");
		private TextField txtArquivo1 = new TextField();
		private TextField txtArquivo2 = new TextField();

		Controle() {
			super(new GridLayout(2, 2));
			add(criarPainel(lblArquivo1, txtArquivo1, btnArquivo1));
			add(criarPainel(lblArquivo2, txtArquivo2, btnArquivo2));
		}

		private Panel criarPainel(JComponent c1, JComponent c2, JComponent c3) {
			Panel panel = new Panel();
			panel.add(BorderLayout.WEST, c1);
			panel.add(BorderLayout.CENTER, c2);
			panel.add(BorderLayout.EAST, c3);

			return panel;
		}
	}
}