package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextArea;
import br.com.persist.comp.TextField;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

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

	private class Controle extends Panel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private Button btnComparar = new Button("label.comparar");
		private Button btnArquivo1 = new Button("label.procurar");
		private Button btnArquivo2 = new Button("label.procurar");
		private Label lblArquivo1 = new Label("label.arquivo1");
		private Label lblArquivo2 = new Label("label.arquivo2");
		private TextField txtArquivo1 = new TextField();
		private TextField txtArquivo2 = new TextField();
		private Label lblStatus = new Label();

		Controle() {
			super(new GridLayout(3, 1));
			add(criarPainel(lblArquivo1, txtArquivo1, btnArquivo1));
			add(criarPainel(lblArquivo2, txtArquivo2, btnArquivo2));
			add(criarPainel(btnComparar, lblStatus, new Label()));
			btnArquivo1.addActionListener(this);
			btnArquivo2.addActionListener(this);
			btnComparar.addActionListener(this);
		}

		private Panel criarPainel(JComponent c1, JComponent c2, JComponent c3) {
			Panel panel = new Panel();
			panel.add(BorderLayout.WEST, c1);
			panel.add(BorderLayout.CENTER, c2);
			panel.add(BorderLayout.EAST, c3);

			return panel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnArquivo1) {
				selecionar(txtArquivo1);
			} else if (e.getSource() == btnArquivo2) {
				selecionar(txtArquivo2);
			} else if (e.getSource() == btnComparar) {
				// comparar();
			}
		}

		private void selecionar(TextField txt) {
			File file = getSelectedFile();

			if (file == null) {
				return;
			}

			txt.setText(file.getAbsolutePath());
		}

		private File getSelectedFile() {
			JFileChooser fileChooser = Util.criarFileChooser(null, false);
			int opcao = fileChooser.showOpenDialog(ComparacaoContainer.this);

			if (opcao == JFileChooser.APPROVE_OPTION) {
				return fileChooser.getSelectedFile();
			}

			return null;
		}
	}
}