package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;

import javax.swing.JTable;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.editor.ChaveValorEditor;
import br.com.persist.fichario.Fichario;
import br.com.persist.modelo.VariaveisModelo;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class VariaveisContainer extends Panel implements IIni, Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final VariaveisModelo modelo = new VariaveisModelo();
	private final JTable tabela = new JTable(modelo);
	private final Toolbar toolbar = new Toolbar();

	public VariaveisContainer(IJanela janela) {
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(1).setCellEditor(new ChaveValorEditor());
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
		private Action novoAcao = Action.actionIconNovo();

		public void ini(IJanela janela) {
			super.ini(janela, false, true);
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_VARIAVEL);
			configBaixarAcao(null);

			addButton(true, novoAcao);
			addButton(copiarAcao);

			eventos();
		}

		@Override
		protected void salvar() {
			try {
				VariaveisModelo.salvar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR: ", ex, VariaveisContainer.this);
			}
		}

		private void eventos() {
			baixarAcao.setActionListener(e -> {
				VariaveisModelo.inicializar();
				modelo.fireTableDataChanged();
				TabelaUtil.ajustar(tabela, getGraphics());
			});

			novoAcao.setActionListener(e -> {
				VariaveisModelo.novo();
				modelo.fireTableDataChanged();
			});

			copiarAcao.setActionListener(e -> {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					for (int i : linhas) {
						ChaveValor cv = VariaveisModelo.getChaveValor(i);
						ChaveValor clone = cv.clonar();
						clone.setChave(cv.getChave() + "_" + Constantes.TEMP);
						VariaveisModelo.adicionar(clone);
					}

					modelo.fireTableDataChanged();
				}
			});
		}
	}
}