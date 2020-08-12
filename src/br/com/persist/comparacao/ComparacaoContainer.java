package br.com.persist.comparacao;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.TextField;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.Fichario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class ComparacaoContainer extends AbstratoContainer implements Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private ComparacaoFormulario comparacaoFormulario;
	private final TextArea textArea = new TextArea();
	private final Controle controle = new Controle();
	private final Toolbar toolbar = new Toolbar();

	public ComparacaoContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public ComparacaoFormulario getComparacaoFormulario() {
		return comparacaoFormulario;
	}

	public void setComparacaoFormulario(ComparacaoFormulario comparacaoFormulario) {
		this.comparacaoFormulario = comparacaoFormulario;
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, controle);
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
				comparar();
			}
		}

		private void selecionar(TextField txt) {
			File parent = null;

			if (!Util.estaVazio(txt.getText())) {
				File f = new File(txt.getText());

				if (f.exists()) {
					parent = f.getParentFile();
				}
			}

			File file = getSelectedFile(parent);

			if (file == null) {
				return;
			}

			txt.setText(file.getAbsolutePath());
		}

		private File getSelectedFile(File parent) {
			JFileChooser fileChooser = Util.criarFileChooser(parent, false);
			int opcao = fileChooser.showOpenDialog(ComparacaoContainer.this);

			if (opcao == JFileChooser.APPROVE_OPTION) {
				return fileChooser.getSelectedFile();
			}

			return null;
		}

		private void comparar() {
			lblStatus.setText(Constantes.VAZIO);

			File file1 = new File(txtArquivo1.getText());

			if (!file1.exists()) {
				lblStatus.setText("Arquivo 1 inexistente!");
				return;
			}

			File file2 = new File(txtArquivo2.getText());

			if (!file2.exists()) {
				lblStatus.setText("Arquivo 2 inexistente!");
				return;
			}

			List<List<String>> listas = Util.comparar(file1, file2);

			lblStatus.setText("Comparado");
			textArea.setText(Constantes.VAZIO);

			for (List<String> list : listas) {
				for (String string : list) {
					textArea.append(string + Constantes.QL);
				}
			}
		}
	}

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getComparacao().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getComparacao().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		ComparacaoFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (comparacaoFormulario != null) {
			comparacaoFormulario.retornoAoFichario();
		}
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(IJanela janela) {
			super.ini(janela, true, false);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_COMPARACAO);
		}

		@Override
		protected void limpar() {
			controle.lblStatus.setText(Constantes.VAZIO);
			textArea.limpar();
		}
	}
}