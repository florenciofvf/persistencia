package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.comp.Button;
import br.com.persist.comp.ScrollPane;
import br.com.persist.formulario.Formulario;
import br.com.persist.tabela.ModeloConexao;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class DialogoConexao extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final ModeloConexao modelo = new ModeloConexao();
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;

	public DialogoConexao(Formulario formulario) {
		super(formulario, Mensagens.getString("label.conexao"), 700, 200, false);
		this.formulario = formulario;
		montarLayout();
		setVisible(true);
		SwingUtilities.invokeLater(() -> toFront());
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(new JTable(modelo)));
	}

	protected void processar() {
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new NovoAcao()));
			add(new Button(new AbrirAcao()));
			add(new Button(new SalvarAcao()));
		}
	}

	private class NovoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public NovoAcao() {
			super(false, "label.novo", Icones.NOVO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			modelo.novo();
		}
	}

	private class AbrirAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public AbrirAcao() {
			super(false, "label.baixar", Icones.BAIXAR);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				modelo.abrir();
				formulario.atualizarConexoes();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ABRIR: ", ex, DialogoConexao.this);
			}
		}
	}

	private class SalvarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SalvarAcao() {
			super(false, "label.salvar", Icones.SALVAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				modelo.salvar();
				formulario.atualizarConexoes();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR: ", ex, DialogoConexao.this);
			}
		}
	}
}