package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JToolBar;

import br.com.persist.comp.Button;
import br.com.persist.comp.ScrollPane;
import br.com.persist.principal.Formulario;
import br.com.persist.modelo.FragmentoModelo;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.Fragmento;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class FragmentoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final FragmentoModelo modelo = new FragmentoModelo();
	private final JTable tabela = new JTable(modelo);
	private final Toolbar toolbar = new Toolbar();
	private final FragmentoListener listener;

	public FragmentoDialogo(Formulario formulario, FragmentoListener listener) {
		super(formulario, Mensagens.getString("label.fragmento"), 1000, 600, false);
		this.listener = listener;
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		toolbar.configListener();
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				toolbar.abrirAcao.actionPerformed(null);
			}
		});
	}

	protected void processar() {
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action salvarAcao = Action.actionIcon("label.salvar", Icones.SALVAR);
		private Action abrirAcao = Action.actionIcon("label.baixar", Icones.BAIXAR);
		private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
		private Action novoAcao = Action.actionIcon("label.novo", Icones.NOVO);

		Toolbar() {
			add(new Button(novoAcao));
			add(new Button(copiarAcao));
			addSeparator();
			add(new Button(abrirAcao));
			add(new Button(salvarAcao));

			eventos();
		}

		private void eventos() {
			abrirAcao.setActionListener(e -> {
				if (listener != null) {
					FragmentoModelo.reiniciar();
					FragmentoModelo.filtar(listener.getGruposFiltro());
				} else {
					try {
						FragmentoModelo.inicializar();
					} catch (Exception ex) {
						Util.stackTraceAndMessage("ATUALIZAR FRAGMENTOS", ex, FragmentoDialogo.this);
					}
				}

				FragmentoModelo.ordenar();
				modelo.fireTableDataChanged();
				TabelaUtil.ajustar(tabela, getGraphics(), 40);
			});

			novoAcao.setActionListener(e -> {
				FragmentoModelo.novo();
				modelo.fireTableDataChanged();
			});

			copiarAcao.setActionListener(e -> {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					for (int i : linhas) {
						Fragmento f = FragmentoModelo.getFragmento(i);
						FragmentoModelo.adicionar(f.clonar());
					}

					modelo.fireTableDataChanged();
				}
			});

			salvarAcao.setActionListener(e -> {
				try {
					FragmentoModelo.salvar();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SALVAR: ", ex, FragmentoDialogo.this);
				}
			});
		}

		void configListener() {
			if (listener != null) {
				Action configAcao = Action.actionIcon("label.fragmento", Icones.SUCESSO, e -> {
					int[] linhas = tabela.getSelectedRows();

					if (linhas != null && linhas.length == 1) {
						Fragmento f = FragmentoModelo.getFragmento(linhas[0]);
						listener.configFragmento(f);
						dispose();
					}
				});

				add(new Button(configAcao));
			}
		}
	}

	public static interface FragmentoListener {
		public void configFragmento(Fragmento f);

		public List<String> getGruposFiltro();
	}
}