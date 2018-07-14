package br.com.persist.tabela;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class Tabela extends JTable {
	private static final long serialVersionUID = 1L;
	private PopupHeader popupHeader = new PopupHeader();

	public Tabela(ModeloOrdenacao modelo) {
		super(modelo);
		tableHeader.addMouseListener(headerListener);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (!(dataModel instanceof ModeloOrdenacao)) {
			throw new IllegalStateException();
		}

		super.setModel(dataModel);
	}

	private MouseListener headerListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			if (!e.isPopupTrigger()) {
				return;
			}

			int tableColuna = columnAtPoint(e.getPoint());
			int modelColuna = convertColumnIndexToModel(tableColuna);
			popupHeader.setTag(modelColuna);
			popupHeader.show(tableHeader, e.getX(), e.getY());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				int tableColuna = columnAtPoint(e.getPoint());
				TableColumnModel columnModel = getColumnModel();
				TableColumn tableColumn = columnModel.getColumn(tableColuna);
				CabecalhoColuna cabecalho = (CabecalhoColuna) tableColumn.getHeaderRenderer();

				int resto = getResto(e.getX(), tableColumn);

				if (cabecalho.isOrdenacao(resto)) {
					cabecalho.ordenar();
				} else if (cabecalho.isFiltro(resto, tableColumn.getWidth())) {
					cabecalho.filtrar(e.getXOnScreen() - resto, e.getYOnScreen() + tableHeader.getHeight() - e.getY());
				} else {
					cabecalho.ordenar();
				}
			}
		}

		private int getResto(int x, TableColumn tableColumn) {
			TableColumnModel columnModel = getColumnModel();
			int total = columnModel.getColumnCount();
			int soma = 0;

			for (int c = 0; c < total; c++) {
				TableColumn coluna = columnModel.getColumn(c);

				if (tableColumn == coluna) {
					return x - soma;
				}

				soma += coluna.getWidth();
			}

			return -1;
		}
	};

	private class PopupHeader extends Popup {
		private static final long serialVersionUID = 1L;
		final MenuItem itemCopiarComAspas = new MenuItem("label.copiar_com_aspas");
		final MenuItem itemCopiar = new MenuItem("label.copiar");
		private int tag;

		public PopupHeader() {
			add(itemCopiar);
			addSeparator();
			add(itemCopiarComAspas);

			itemCopiar.addActionListener(e -> {
				List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, getTag());
				Util.setContentTransfered(Util.getStringLista(lista, false));
			});

			itemCopiarComAspas.addActionListener(e -> {
				List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, getTag());
				Util.setContentTransfered(Util.getStringLista(lista, true));
			});
		}

		public int getTag() {
			return tag;
		}

		public void setTag(int tag) {
			this.tag = tag;
		}
	}
}