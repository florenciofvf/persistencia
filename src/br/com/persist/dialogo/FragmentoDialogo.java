package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import br.com.persist.comp.Button;
import br.com.persist.comp.ScrollPane;
import br.com.persist.formulario.Formulario;
import br.com.persist.modelo.FragmentoModelo;
import br.com.persist.util.Acao;
import br.com.persist.util.Fragmento;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class FragmentoDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final FragmentoModelo modelo = new FragmentoModelo();
	private final JTable tabela = new JTable(modelo);
	private final Toolbar toolbar = new Toolbar();
	private final FragmentoListener listener;

	public FragmentoDialogo(Formulario formulario, FragmentoListener listener) {
		super(formulario, Mensagens.getString("label.fragmento"), 1000, 500, false);
		this.listener = listener;
		montarLayout();
		configurar();
		setVisible(true);
	}

	private void montarLayout() {
		toolbar.configListener();
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
			add(new Button(new NovoAcao()));
			add(new Button(new CopiaAcao()));
			addSeparator();
			add(new Button(new AbrirAcao()));
			add(new Button(new SalvarAcao()));
		}

		void configListener() {
			if (listener != null) {
				add(new Button(new ConfigFragmentoAcao()));
			}
		}
	}

	private class ConfigFragmentoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ConfigFragmentoAcao() {
			super(false, "label.fragmento", Icones.SUCESSO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length == 1) {
				Fragmento f = FragmentoModelo.getFragmento(linhas[0]);
				listener.configFragmento(f);
				dispose();
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
			FragmentoModelo.novo();
			modelo.fireTableDataChanged();
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
					Fragmento f = FragmentoModelo.getFragmento(i);
					FragmentoModelo.adicionar(f.clonar());
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
			FragmentoModelo.reiniciar();

			if (listener != null) {
				FragmentoModelo.filtar(listener.getGruposFiltro());
			}

			modelo.fireTableDataChanged();
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
				FragmentoModelo.salvar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR: ", ex, FragmentoDialogo.this);
			}
		}
	}

	public static interface FragmentoListener {
		public void configFragmento(Fragmento f);

		public List<String> getGruposFiltro();
	}
}