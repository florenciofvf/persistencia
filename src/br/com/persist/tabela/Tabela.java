package br.com.persist.tabela;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.modelo.OrdenacaoModelo;
import br.com.persist.modelo.VazioModelo;
import br.com.persist.util.Acao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class Tabela extends JTable {
	private static final long serialVersionUID = 1L;
	private PopupHeader popupHeader = new PopupHeader();
	private Map<String, List<String>> mapaChaveamento;

	public Tabela() {
		this(new OrdenacaoModelo(new VazioModelo()));
	}

	public Tabela(OrdenacaoModelo modelo) {
		super(modelo);
		tableHeader.addMouseListener(headerListener);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (!(dataModel instanceof OrdenacaoModelo)) {
			throw new IllegalStateException();
		}

		super.setModel(dataModel);
	}

	public Map<String, List<String>> getMapaChaveamento() {
		return mapaChaveamento;
	}

	public void setMapaChaveamento(Map<String, List<String>> mapaChaveamento) {
		this.mapaChaveamento = mapaChaveamento;
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
			popupHeader.tag = modelColuna;
			String coluna = getModel().getColumnName(modelColuna);
			popupHeader.configurar(coluna);
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
		private MenuCopiarIN menuCopiarIN = new MenuCopiarIN();
		private int tag;

		public PopupHeader() {
			add(new MenuItem(new CopiarNomeAcao()));
			add(new MenuCopiar());
			addSeparator();
			add(menuCopiarIN);
		}

		private class CopiarNomeAcao extends Acao {
			private static final long serialVersionUID = 1L;

			public CopiarNomeAcao() {
				super(true, "label.copiar_nome_coluna", null);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String coluna = getModel().getColumnName(tag);
				Util.setContentTransfered(coluna);
			}
		}

		private class MenuCopiar extends Menu {
			private static final long serialVersionUID = 1L;

			MenuCopiar() {
				super("label.copiar_header");
				add(new MenuItem(new SemAspasAcao()));
				add(new MenuItem(new ComAspasAcao()));
			}

			private class SemAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				public SemAspasAcao() {
					super(true, "label.sem_aspas", null);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
					Util.setContentTransfered(Util.getStringLista(lista, false));
				}
			}

			private class ComAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				public ComAspasAcao() {
					super(true, "label.com_aspas", Icones.ASPAS);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
					Util.setContentTransfered(Util.getStringLista(lista, true));
				}
			}
		}

		private class MenuCopiarIN extends Menu {
			private static final long serialVersionUID = 1L;

			MenuCopiarIN() {
				super("label.vazio");
				add(new MenuItem(new SemAspasAcao()));
				add(new MenuItem(new ComAspasAcao()));
			}

			private class SemAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				public SemAspasAcao() {
					super(true, "label.sem_aspas", null);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
					String complemento = Util.getStringLista(lista, false);

					if (!Util.estaVazio(complemento)) {
						String coluna = Tabela.this.getModel().getColumnName(tag);
						Util.setContentTransfered("AND " + coluna + " IN (" + complemento + ")");
					} else {
						Util.setContentTransfered(" ");
					}
				}
			}

			private class ComAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				public ComAspasAcao() {
					super(true, "label.com_aspas", Icones.ASPAS);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
					String complemento = Util.getStringLista(lista, true);

					if (!Util.estaVazio(complemento)) {
						String coluna = Tabela.this.getModel().getColumnName(tag);
						Util.setContentTransfered("AND " + coluna + " IN (" + complemento + ")");
					} else {
						Util.setContentTransfered(" ");
					}
				}
			}
		}

		private class MenuCopiarIN2 extends Menu {
			private static final long serialVersionUID = 1L;
			private final String nomeColuna;

			MenuCopiarIN2(String coluna) {
				super("label.vazio");
				add(new MenuItem(new SemAspasAcao()));
				add(new MenuItem(new ComAspasAcao()));
				setText("AND IN - " + coluna);
				this.nomeColuna = coluna;
			}

			private class SemAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				public SemAspasAcao() {
					super(true, "label.sem_aspas", null);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
					String complemento = Util.getStringLista(lista, false);

					if (!Util.estaVazio(complemento)) {
						Util.setContentTransfered("AND " + nomeColuna + " IN (" + complemento + ")");
					} else {
						Util.setContentTransfered(" ");
					}
				}
			}

			private class ComAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				public ComAspasAcao() {
					super(true, "label.com_aspas", Icones.ASPAS);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
					String complemento = Util.getStringLista(lista, true);

					if (!Util.estaVazio(complemento)) {
						Util.setContentTransfered("AND " + nomeColuna + " IN (" + complemento + ")");
					} else {
						Util.setContentTransfered(" ");
					}
				}
			}
		}

		private void limparMenuTemp() {
			MenuCopiarIN2 menu = getMenuCopiarIN2();

			while (menu != null) {
				remove(menu);
				menu = getMenuCopiarIN2();
			}
		}

		public void configurar(String chave) {
			menuCopiarIN.setText("AND IN - " + chave);
			List<String> lista = mapaChaveamento.get(chave);
			limparMenuTemp();

			if (lista != null && !lista.isEmpty()) {
				for (String coluna : lista) {
					add(new MenuCopiarIN2(coluna));
				}
			}
		}

		private MenuCopiarIN2 getMenuCopiarIN2() {
			for (int i = 0; i < getComponentCount(); i++) {
				Component c = getComponent(i);

				if (c instanceof MenuCopiarIN2) {
					return (MenuCopiarIN2) c;
				}
			}

			return null;
		}
	}
}