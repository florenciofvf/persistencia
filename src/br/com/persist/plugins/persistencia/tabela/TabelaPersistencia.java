package br.com.persist.plugins.persistencia.tabela;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.componente.MenuItem;
import br.com.persist.componente.Popup;
import br.com.persist.plugins.mapeamento.Mapeamento;
import br.com.persist.plugins.mapeamento.MapeamentoProvedor;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.PersistenciaModelo;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.MenuPadrao2;

public class TabelaPersistencia extends JTable {
	private static final long serialVersionUID = 1L;
	private transient TabelaPersistenciaListener listener;
	private PopupHeader popupHeader = new PopupHeader();
	private Map<String, List<String>> chaveamento;
	private Map<String, String> mapeamento;
	private boolean arrastado;

	public TabelaPersistencia() {
		this(new OrdenacaoModelo(PersistenciaModelo.criarVazio()));
	}

	public TabelaPersistencia(OrdenacaoModelo modelo) {
		super(modelo);
		tableHeader.addMouseListener(headerListenerInner);
		addMouseMotionListener(mouseMotionListenerInner);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		addMouseListener(mouseListenerInner);
		chaveamento = new HashMap<>();
		mapeamento = new HashMap<>();
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (!(dataModel instanceof OrdenacaoModelo)) {
			throw new IllegalStateException("PersistenciaOrdenacaoModelo inconsistente.");
		}

		super.setModel(dataModel);
	}

	public String getNomeColunas() {
		return getModelo().getNomeColunas();
	}

	public OrdenacaoModelo getModelo() {
		return (OrdenacaoModelo) getModel();
	}

	public void setTabelaPersistenciaListener(TabelaPersistenciaListener listener) {
		this.listener = listener;
	}

	public Map<String, String> getMapeamento() {
		if (mapeamento == null) {
			mapeamento = new HashMap<>();
		}
		return mapeamento;
	}

	public void setMapeamento(Map<String, String> mapeamento) {
		this.mapeamento = mapeamento;
	}

	public Map<String, List<String>> getChaveamento() {
		if (chaveamento == null) {
			chaveamento = new HashMap<>();
		}
		return chaveamento;
	}

	public void setChaveamento(Map<String, List<String>> chaveamento) {
		this.chaveamento = chaveamento;
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
			if (listener != null) {
				int tableColuna = columnAtPoint(e.getPoint());
				int modelColuna = convertColumnIndexToModel(tableColuna);
				listener.tabelaMouseClick(TabelaPersistencia.this, modelColuna);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (arrastado && listener != null) {
				arrastado = false;
				int tableColuna = columnAtPoint(e.getPoint());
				int modelColuna = convertColumnIndexToModel(tableColuna);
				listener.tabelaMouseClick(TabelaPersistencia.this, modelColuna);
			}
		}
	};

	private transient MouseListener headerListenerInner = new MouseAdapter() {
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
			popupHeader.indiceColuna = modelColuna;
			popupHeader.preShow(getModel().getColumnName(modelColuna));
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
		private Action concatNomeColunaAcao = Action.actionMenu("label.concatenar_nome_coluna", null);
		private Action copiarNomeColunaAcao = Action.actionMenu("label.copiar_nome_coluna", null);
		private Action detalheColunaAcao = Action.actionMenu("label.meta_dados", Icones.INFO);
		private ItemMapeamento itemMapeamento = new ItemMapeamento();
		private Separator separator = new Separator();
		private MenuIN menuIN = new MenuIN();
		private int indiceColuna;

		private PopupHeader() {
			addMenuItem(detalheColunaAcao);
			add(true, new MenuConcatenado("label.copiar_nome_coluna_concat_n", true, false));
			add(new MenuConcatenado("label.copiar_nome_coluna_concat_l", false, true));
			add(new MenuConcatenado("label.copiar_nome_coluna_concat", false, false));
			addMenuItem(true, copiarNomeColunaAcao);
			addMenuItem(true, concatNomeColunaAcao);
			add(true, new MenuCopiarLinhas());
			add(true, menuIN);

			eventos();
		}

		private void eventos() {
			copiarNomeColunaAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(indiceColuna);
				Util.setContentTransfered(coluna);

				if (listener != null && Preferencias.isCopiarNomeColunaListener()) {
					listener.copiarNomeColuna(TabelaPersistencia.this, coluna, null);
				}
			});

			concatNomeColunaAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(indiceColuna);

				if (listener != null) {
					listener.concatenarNomeColuna(TabelaPersistencia.this, coluna);
				}
			});

			detalheColunaAcao.setActionListener(e -> {
				Coluna coluna = ((OrdenacaoModelo) getModel()).getColuna(indiceColuna);

				if (coluna != null) {
					Util.mensagem(TabelaPersistencia.this, coluna.getDetalhe());
				}
			});
		}

		private void preShow(String chave) {
			menuIN.setText("AND IN - " + chave);

			limparMenuChaveamento();
			List<String> lista = getChaveamento().get(chave);
			if (lista != null) {
				for (String coluna : lista) {
					add(new MenuItemChaveamento(coluna));
				}
			}

			limparItemMapeamento();
			String valorChave = getMapeamento().get(chave);
			if (valorChave != null) {
				itemMapeamento.setText(valorChave);
				add(separator);
				add(itemMapeamento);
			}
		}

		private class MenuCopiarLinhas extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;
			private Action comAspasQLSemVirgulaAcao = Action.actionMenu("label.com_aspas_em_linhas_sem_v",
					Icones.ASPAS);
			private Action semAspasQLSemVirgulaAcao = Action.actionMenu("label.sem_aspas_em_linhas_sem_v", null);
			private Action comAspasQLAcao = Action.actionMenu("label.com_aspas_em_linhas", Icones.ASPAS);
			private Action semAspasQLAcao = Action.actionMenu("label.sem_aspas_em_linhas", null);

			private MenuCopiarLinhas() {
				super("label.copiar_header");

				addSeparator();
				addMenuItem(semAspasQLAcao);
				addMenuItem(comAspasQLAcao);
				addSeparator();
				addMenuItem(semAspasQLSemVirgulaAcao);
				addMenuItem(comAspasQLSemVirgulaAcao);

				semAspasQLSemVirgulaAcao.setActionListener(e -> copiarSemV(false));
				comAspasQLSemVirgulaAcao.setActionListener(e -> copiarSemV(true));
				semAspasQLAcao.setActionListener(e -> copiar(true, false));
				comAspasQLAcao.setActionListener(e -> copiar(true, true));
				semAspasAcao.setActionListener(e -> copiar(false, false));
				comAspasAcao.setActionListener(e -> copiar(false, true));
			}

			private void copiar(boolean emLinhas, boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinhaPelaColuna(TabelaPersistencia.this,
						indiceColuna);
				Util.setContentTransfered(Util.getStringLista(lista, aspas, emLinhas));
			}

			private void copiarSemV(boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinhaPelaColuna(TabelaPersistencia.this,
						indiceColuna);
				Util.setContentTransfered(Util.getStringListaSemVirgula(lista, aspas));
			}
		}

		private class MenuConcatenado extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;
			private final boolean numeros;
			private final boolean letras;

			private MenuConcatenado(String titulo, boolean num, boolean let) {
				super(titulo);
				numeros = num;
				letras = let;
				semAspasAcao.setActionListener(e -> copiar(false));
				comAspasAcao.setActionListener(e -> copiar(true));
			}

			private void copiar(boolean aspas) {
				String string = Util.getContentTransfered();

				String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
				Util.setContentTransfered(coluna);

				if (listener != null && Preferencias.isCopiarNomeColunaListener()) {
					if (numeros) {
						string = Util.soNumeros(string);
					}

					if (letras) {
						string = Util.soLetras(string);
					}

					if (aspas && !Util.estaVazio(string)) {
						string = Util.citar(string);
					}

					listener.copiarNomeColuna(TabelaPersistencia.this, coluna, string);
				}
			}
		}

		private class MenuIN extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;

			private MenuIN() {
				super(Constantes.LABEL_VAZIO);

				semAspasAcao.setActionListener(e -> copiarIN(false));
				comAspasAcao.setActionListener(e -> copiarIN(true));
			}

			private void copiarIN(boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinhaPelaColuna(TabelaPersistencia.this,
						indiceColuna);
				String complemento = Util.getStringLista(lista, aspas, false);

				if (!Util.estaVazio(complemento)) {
					String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
					Util.setContentTransfered("AND " + coluna + " IN (" + complemento + ")");

				} else {
					Util.setContentTransfered(" ");
				}
			}
		}

		private class MenuItemChaveamento extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;
			private final String nomeColuna;

			private MenuItemChaveamento(String coluna) {
				super(Constantes.LABEL_VAZIO);
				setText("AND IN - " + coluna);
				this.nomeColuna = coluna;

				semAspasAcao.setActionListener(e -> copiarINDinamico(false));
				comAspasAcao.setActionListener(e -> copiarINDinamico(true));
			}

			private void copiarINDinamico(boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinhaPelaColuna(TabelaPersistencia.this,
						indiceColuna);
				String complemento = Util.getStringLista(lista, aspas, false);

				if (!Util.estaVazio(complemento)) {
					Util.setContentTransfered("AND " + nomeColuna + " IN (" + complemento + ")");
				} else {
					Util.setContentTransfered(" ");
				}
			}
		}

		private MenuItemChaveamento getPrimeiroMenuItemChaveamento() {
			for (int i = 0; i < getComponentCount(); i++) {
				Component c = getComponent(i);

				if (c instanceof MenuItemChaveamento) {
					return (MenuItemChaveamento) c;
				}
			}

			return null;
		}

		private void limparMenuChaveamento() {
			MenuItemChaveamento menu = getPrimeiroMenuItemChaveamento();

			while (menu != null) {
				remove(menu);
				menu = getPrimeiroMenuItemChaveamento();
			}
		}

		private void limparItemMapeamento() {
			remove(separator);
			remove(itemMapeamento);
		}

		private class ItemMapeamento extends MenuItem {
			private static final long serialVersionUID = 1L;

			private ItemMapeamento() {
				super(Constantes.LABEL_VAZIO);

				addActionListener(e -> {
					Mapeamento m = MapeamentoProvedor.getMapeamento(getText());
					Util.mensagem(TabelaPersistencia.this, m == null ? Constantes.VAZIO : m.getValor());
				});
			}
		}
	}
}