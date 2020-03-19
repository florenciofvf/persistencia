package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;

import javax.swing.JTable;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.listener.FragmentoListener;
import br.com.persist.modelo.FragmentoModelo;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Fragmento;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class FragmentoContainer extends Panel implements IIni, Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final FragmentoModelo modelo = new FragmentoModelo();
	private final transient FragmentoListener listener;
	private final JTable tabela = new JTable(modelo);
	private final Toolbar toolbar = new Toolbar();

	public FragmentoContainer(IJanela janela, FragmentoListener listener) {
		this.listener = listener;
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		toolbar.configListener();
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.getBaixarAcao().actionPerformed(null);
	}

	@Override
	public void ini(Graphics graphics) {
		TabelaUtil.ajustar(tabela, graphics);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
		private Action salvarAcao = Action.actionIconSalvar();
		private Action novoAcao = Action.actionIconNovo();

		public void ini(IJanela janela) {
			super.ini(janela, false);
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_FRAGMENTO);
			configBaixarAcao(null);

			addButton(salvarAcao);
			addButton(true, novoAcao);
			addButton(copiarAcao);

			eventos();
		}

		private void eventos() {
			baixarAcao.setActionListener(e -> {
				if (listener != null) {
					FragmentoModelo.reiniciar();
					FragmentoModelo.filtar(listener.getGruposFiltro());
				} else {
					FragmentoModelo.inicializar();
				}

				FragmentoModelo.ordenar();
				modelo.fireTableDataChanged();
				TabelaUtil.ajustar(tabela, getGraphics());
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
					Util.stackTraceAndMessage("SALVAR: ", ex, FragmentoContainer.this);
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

						if (janela != null) {
							janela.fechar();
						}
					}
				});

				addButton(configAcao);
			}
		}
	}
}