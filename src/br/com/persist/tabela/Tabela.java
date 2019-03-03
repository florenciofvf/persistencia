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
	private TabelaListener tabelaListener;

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

	public TabelaListener getTabelaListener() {
		return tabelaListener;
	}

	public void setTabelaListener(TabelaListener tabelaListener) {
		this.tabelaListener = tabelaListener;
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

		PopupHeader() {
			add(new MenuItem(new InformacaoAcao()));
			addSeparator();
			add(new MenuItem(new CopiarNomeAcao()));
			add(new MenuCopiarValor());
			addSeparator();
			add(menuCopiarIN);
		}

		class InformacaoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			InformacaoAcao() {
				super(true, "label.meta_dados", Icones.INFO);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Coluna coluna = ((OrdenacaoModelo) getModel()).getColuna(tag);

				if (coluna != null) {
					Util.mensagem(Tabela.this, coluna.getDetalhe());
				}
			}
		}

		class CopiarNomeAcao extends Acao {
			private static final long serialVersionUID = 1L;

			CopiarNomeAcao() {
				super(true, "label.copiar_nome_coluna", null);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String coluna = getModel().getColumnName(tag);
				Util.setContentTransfered(coluna);

				if (tabelaListener != null && Constantes.tabela_listener) {
					tabelaListener.copiarNomeColuna(Tabela.this, coluna);
				}
			}
		}

		class MenuCopiarValor extends Menu {
			private static final long serialVersionUID = 1L;

			MenuCopiarValor() {
				super("label.copiar_header");
				add(new MenuItem(new SemAspasAcao()));
				add(new MenuItem(new ComAspasAcao()));
			}

			class SemAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				SemAspasAcao() {
					super(true, "label.sem_aspas", null);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
					Util.setContentTransfered(Util.getStringLista(lista, false));
				}
			}

			class ComAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				ComAspasAcao() {
					super(true, "label.com_aspas", Icones.ASPAS);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> lista = TabelaUtil.getValoresColuna(Tabela.this, tag);
					Util.setContentTransfered(Util.getStringLista(lista, true));
				}
			}
		}

		class MenuCopiarIN extends Menu {
			private static final long serialVersionUID = 1L;

			MenuCopiarIN() {
				super("label.vazio");
				add(new MenuItem(new SemAspasAcao()));
				add(new MenuItem(new ComAspasAcao()));
			}

			class SemAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				SemAspasAcao() {
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

			class ComAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				ComAspasAcao() {
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

		class MenuCopiarINDinamico extends Menu {
			private static final long serialVersionUID = 1L;
			private final String nomeColuna;

			MenuCopiarINDinamico(String coluna) {
				super("label.vazio");
				add(new MenuItem(new SemAspasAcao()));
				add(new MenuItem(new ComAspasAcao()));
				setText("AND IN - " + coluna);
				this.nomeColuna = coluna;
			}

			class SemAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				SemAspasAcao() {
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

			class ComAspasAcao extends Acao {
				private static final long serialVersionUID = 1L;

				ComAspasAcao() {
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

		public void configurar(String chave) {
			menuCopiarIN.setText("AND IN - " + chave);
			List<String> lista = mapaChaveamento.get(chave);
			limparMenuDinamico();

			if (lista != null && !lista.isEmpty()) {
				for (String coluna : lista) {
					add(new MenuCopiarINDinamico(coluna));
				}
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
	}
}