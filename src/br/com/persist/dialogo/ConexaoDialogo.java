package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import br.com.persist.banco.Conexao;
import br.com.persist.comp.Button;
import br.com.persist.comp.ScrollPane;
import br.com.persist.formulario.Formulario;
import br.com.persist.modelo.ModeloConexao;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class ConexaoDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final ModeloConexao modelo = new ModeloConexao();
	private final JTable tabela = new JTable(modelo);
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;

	public ConexaoDialogo(Formulario formulario) {
		super(formulario, Mensagens.getString("label.conexao"), 700, 200, false);
		this.formulario = formulario;
		montarLayout();
		configurar();
		setVisible(true);
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				new AbrirAcao().actionPerformed(null);
			}
		});
	}

	protected void processar() {
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new TopAcao()));
			addSeparator();
			add(new Button(new FecharAcao()));
			addSeparator();
			add(new Button(new NovoAcao()));
			add(new Button(new CopiaAcao()));
			addSeparator();
			add(new Button(new AbrirAcao()));
			add(new Button(new SalvarAcao()));
		}
	}

	private class TopAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public TopAcao() {
			super(false, "label.primeiro", Icones.TOP);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length == 1 && modelo.getColumnCount() > 1 && linhas[0] > 0) {
				modelo.primeiro(linhas[0]);
				modelo.fireTableDataChanged();
				tabela.setRowSelectionInterval(0, 0);
			}
		}
	}

	private class FecharAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FecharAcao() {
			super(false, "label.final_conexoes", Icones.BANCO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Conexao.fecharConexoes();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(getClass().getName() + ".fechar()", ex, formulario);
			}
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

	private class CopiaAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CopiaAcao() {
			super(false, "label.copiar", Icones.COPIA);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length > 0) {
				for (int i : linhas) {
					Conexao c = modelo.getConexao(i);
					modelo.adicionar(c.clonar());
				}

				modelo.fireTableDataChanged();
			}
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
				Util.stackTraceAndMessage("ABRIR: ", ex, ConexaoDialogo.this);
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
				Util.stackTraceAndMessage("SALVAR: ", ex, ConexaoDialogo.this);
			}
		}
	}
}