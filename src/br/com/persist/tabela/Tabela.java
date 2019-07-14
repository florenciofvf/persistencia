package br.com.persist.tabela;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.listener.TabelaListener;
import br.com.persist.modelo.MapeamentoModelo;
import br.com.persist.modelo.OrdenacaoModelo;
import br.com.persist.modelo.VazioModelo;
import br.com.persist.util.Action;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.MenuPadrao2;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;

public class Tabela extends JTable {
	private static final long serialVersionUID = 1L;
	private PopupHeader popupHeader = new PopupHeader();
	private Map<String, List<String>> mapaChaveamento;
	private transient TabelaListener tabelaListener;
	private Map<String, String> mapeamento;
	private boolean arrastado;

	public Tabela() {
		this(new OrdenacaoModelo(new VazioModelo()));
	}

	public Tabela(OrdenacaoModelo modelo) {
		super(modelo);
		addMouseMotionListener(mouseMotionListenerInner);
		tableHeader.addMouseListener(headerListener);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		addMouseListener(mouseListenerInner);
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (!(dataModel instanceof OrdenacaoModelo)) {
			throw new IllegalStateException();
		}

		super.setModel(dataModel);
	}

	public TabelaListener getTabelaListener() {
		return tabelaListener;
	}

	public void setTabelaListener(TabelaListener tabelaListener) {
		this.tabelaListener = tabelaListener;
	}

	public Map<String, String> getMapeamento() {
		return mapeamento;
	}

	public void setMapeamento(Map<String, String> mapeamento) {
		this.mapeamento = mapeamento;
	}

	public Map<String, List<String>> getMapaChaveamento() {
		return mapaChaveamento;
	}

	public void setMapaChaveamento(Map<String, List<String>> mapaChaveamento) {
		this.mapaChaveamento = mapaChaveamento;
	}

	private transient MouseMotionListener mouseMotionListenerInner = new MouseAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			arrastado = true;
		}
	};

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (tabelaListener != null) {
				int tableColuna = columnAtPoint(e.getPoint());
				int modelColuna = convertColumnIndexToModel(tableColuna);
				tabelaListener.tabelaMouseClick(Tabela.this, modelColuna);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (arrastado && tabelaListener != null) {
				arrastado = false;
				int tableColuna = columnAtPoint(e.getPoint());
				int modelColuna = convertColumnIndexToModel(tableColuna);
				tabelaListener.tabelaMouseClick(Tabela.this, modelColuna);
			}
		}
	};

	private transient MouseListener headerListener = new MouseAdapter() {
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
			popupHeader.preShow(coluna);
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
		private Action copiarNomeAcao = Action.actionMenu("label.copiar_nome_coluna", null);
		private Action infoAcao = Action.actionMenu("label.meta_dados", Icones.INFO);
		private MenuCopiarIN menuCopiarIN = new MenuCopiarIN();
		private MenuItemMapa itemMapa = new MenuItemMapa();
		private Separator separator = new Separator();
		private int tag;

		PopupHeader() {
			addMenuItem(infoAcao);
			addMenuItem(true, copiarNomeAcao);
			add(new MenuCopiarValor());
			add(true, menuCopiarIN);

			eventos();
		}

		private void eventos() {
			copiarNomeAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(tag);
				Util.setContentTransfered(coluna);

				if (tabelaListener != null && Preferencias.isCopiarNomeColunaListener()) {
					tabelaListener.copiarNomeColuna(Tabela.this, coluna);
				}
			});

			infoAcao.setActionListener(e -> {
				Coluna coluna = ((OrdenacaoModelo) getModel()).getColuna(tag);

				if (coluna != null) {
					Util.mensagem(Tabela.this, coluna.getDetalhe());
				}
			});
		}

		class MenuCopiarValor extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;

			MenuCopiarValor() {
				super("label.copiar_header");

				semAspasAcao.setActionListener(e -> copiar(false));
				comAspasAcao.setActionListener(e -> copiar(true));
			}

			private void copiar(boolean aspas) {
				List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
				Util.setContentTransfered(Util.getStringLista(lista, aspas));
			}
		}

		class MenuCopiarIN extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;

			MenuCopiarIN() {
				super(Constantes.LABEL_VAZIO);

				semAspasAcao.setActionListener(e -> copiarIN(false));
				comAspasAcao.setActionListener(e -> copiarIN(true));
			}

			private void copiarIN(boolean aspas) {
				List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
				String complemento = Util.getStringLista(lista, aspas);

				if (!Util.estaVazio(complemento)) {
					String coluna = Tabela.this.getModel().getColumnName(tag);
					Util.setContentTransfered("AND " + coluna + " IN (" + complemento + ")");
				} else {
					Util.setContentTransfered(" ");
				}
			}
		}

		class MenuCopiarINDinamico extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;
			private final String nomeColuna;

			MenuCopiarINDinamico(String coluna) {
				super("label.vazio");
				setText("AND IN - " + coluna);
				this.nomeColuna = coluna;

				semAspasAcao.setActionListener(e -> copiarINDinamico(false));
				comAspasAcao.setActionListener(e -> copiarINDinamico(true));
			}

			private void copiarINDinamico(boolean aspas) {
				List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
				String complemento = Util.getStringLista(lista, aspas);

				if (!Util.estaVazio(complemento)) {
					Util.setContentTransfered("AND " + nomeColuna + " IN (" + complemento + ")");
				} else {
					Util.setContentTransfered(" ");
				}
			}
		}

		public void preShow(String chave) {
			menuCopiarIN.setText("AND IN - " + chave);
			List<String> lista = mapaChaveamento.get(chave);
			limparMenuDinamico();

			if (lista != null && !lista.isEmpty()) {
				for (String coluna : lista) {
					add(new MenuCopiarINDinamico(coluna));
				}
			}

			String chaveMapa = mapeamento.get(chave);
			remove(separator);
			remove(itemMapa);

			if (chaveMapa != null) {
				itemMapa.setText(chaveMapa);

				add(separator);
				add(itemMapa);
			}
		}

		private void limparMenuDinamico() {
			MenuCopiarINDinamico menu = getMenuCopiarINDinamico();

			while (menu != null) {
				remove(menu);
				menu = getMenuCopiarINDinamico();
			}
		}

		private MenuCopiarINDinamico getMenuCopiarINDinamico() {
			for (int i = 0; i < getComponentCount(); i++) {
				Component c = getComponent(i);

				if (c instanceof MenuCopiarINDinamico) {
					return (MenuCopiarINDinamico) c;
				}
			}

			return null;
		}

		class MenuItemMapa extends MenuItem {
			private static final long serialVersionUID = 1L;

			public MenuItemMapa() {
				super(Constantes.LABEL_VAZIO);

				addActionListener(e -> {
					ChaveValor cv = MapeamentoModelo.get(getText());
					Util.mensagem(Tabela.this, cv == null ? "" : cv.getValor());
				});
			}
		}
	}
}