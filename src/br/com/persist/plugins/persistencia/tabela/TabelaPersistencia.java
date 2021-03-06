package br.com.persist.plugins.persistencia.tabela;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.MenuPadrao2;
import br.com.persist.componente.Popup;
import br.com.persist.componente.SeparadorDialogo;
import br.com.persist.plugins.mapeamento.Mapeamento;
import br.com.persist.plugins.mapeamento.MapeamentoProvedor;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.PersistenciaModelo;

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

	public String getNomeColunas(String apelido) {
		return getModelo().getNomeColunas(apelido);
	}

	public List<String> getListaNomeColunas(boolean comChaves) {
		return getModelo().getListaNomeColunas(comChaves);
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
			if (e.isPopupTrigger()) {
				int tableColuna = columnAtPoint(e.getPoint());
				int modelColuna = convertColumnIndexToModel(tableColuna);
				popupHeader.indiceColuna = modelColuna;
				popupHeader.preShow(getModel().getColumnName(modelColuna));
				popupHeader.show(tableHeader, e.getX(), e.getY());
			}
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

	public void tornarVisivel(int linha, int coluna) {
		int colunaView = convertColumnIndexToView(coluna);
		Rectangle rect = getCellRect(linha, colunaView, true);
		if (rect != null) {
			scrollRectToVisible(rect);
		}
	}

	public void destacarColuna(int coluna) {
		TableColumn tableColumn = getTableColumn(coluna);
		CabecalhoColuna cabecalho = (CabecalhoColuna) tableColumn.getHeaderRenderer();
		if (cabecalho != null) {
			cabecalho.setBackground(Color.RED);
			SwingUtilities.updateComponentTreeUI(this);
			tornarVisivel(0, coluna);
		}
	}

	public void larguraColuna(int coluna) {
		TableColumn tableColumn = getTableColumn(coluna);
		int atual = tableColumn.getWidth();
		Object resp = Util.getValorInputDialog(TabelaPersistencia.this, "label.largura_coluna", "" + atual, "" + atual);
		if (resp == null || Util.estaVazio(resp.toString())) {
			return;
		}
		tableColumn.setPreferredWidth(Util.getInt(resp.toString(), atual));
	}

	public void larguraTitulo(int coluna, int larguraTitulo) {
		TableColumn tableColumn = getTableColumn(coluna);
		tableColumn.setPreferredWidth(larguraTitulo);
	}

	public void larguraTituloTodos() {
		FontMetrics fontMetrics = getFontMetrics(getFont());
		TableModel model = getModel();
		for (int col = 0; col < model.getColumnCount(); col++) {
			String chave = model.getColumnName(col);
			int largura = fontMetrics.stringWidth(chave) + Constantes.TRINTA;
			larguraTitulo(col, largura);
		}
	}

	public void larguraMinima(int coluna) {
		TableColumn tableColumn = getTableColumn(coluna);
		tableColumn.setPreferredWidth(Constantes.DEZ);
	}

	private TableColumn getTableColumn(int coluna) {
		int colunaView = convertColumnIndexToView(coluna);
		TableColumnModel columnModel = getColumnModel();
		return columnModel.getColumn(colunaView);
	}

	static Action actionMenu(String chave, Icon icon) {
		return Action.acaoMenu(TabelaMensagens.getString(chave), icon);
	}

	static Action actionMenu(String chave) {
		return actionMenu(chave, null);
	}

	private class PopupHeader extends Popup {
		private static final long serialVersionUID = 1L;
		private Action detalheColunaAcao = Action.actionMenu(Constantes.LABEL_METADADOS, Icones.INFO);
		private Action pesquisaApartirColunaAcao = actionMenu("label.pesquisa_a_partir_coluna");
		private Action larguraColunaAcao = Action.actionMenu("label.largura_coluna", null);
		private Action concatNomeColunaAcao = actionMenu("label.concatenar_nome_coluna");
		private Action colocarNomeColunaAcao = actionMenu("label.colocar_nome_coluna");
		private Action copiarNomeColunaAcao = actionMenu("label.copiar_nome_coluna");
		private Action larguraTituloAcao = actionMenu("label.largura_titulo");
		private Action larguraMinimaAcao = actionMenu("label.largura_minima");
		private ItemMapeamento itemMapeamento = new ItemMapeamento();
		private Separator separator = new Separator();
		private MenuIN menuIN = new MenuIN();
		private int larguraColuna;
		private int indiceColuna;

		private PopupHeader() {
			addMenuItem(detalheColunaAcao);
			addMenuItem(true, larguraMinimaAcao);
			addMenuItem(larguraColunaAcao);
			addMenuItem(larguraTituloAcao);
			addMenuItem(true, pesquisaApartirColunaAcao);
			addMenuItem(true, concatNomeColunaAcao);
			addMenuItem(colocarNomeColunaAcao);
			addMenuItem(copiarNomeColunaAcao);
			add(true, new MenuColocarColuna("label.copiar_nome_coluna_concat_n", true, false));
			add(new MenuColocarColuna("label.copiar_nome_coluna_concat_l", false, true));
			add(new MenuColocarColuna("label.copiar_nome_coluna_concat_t", false, false));
			add(true, new MenuCopiarLinhas());
			add(true, menuIN);
			eventos();
		}

		private void eventos() {
			copiarNomeColunaAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(indiceColuna);
				Util.setContentTransfered(coluna);
			});
			pesquisaApartirColunaAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(indiceColuna);
				if (listener != null) {
					listener.pesquisaApartirColuna(TabelaPersistencia.this, coluna);
				}
			});
			concatNomeColunaAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(indiceColuna);
				if (listener != null) {
					listener.concatenarNomeColuna(TabelaPersistencia.this, coluna);
				}
			});
			colocarNomeColunaAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(indiceColuna);
				if (listener != null) {
					listener.colocarNomeColuna(TabelaPersistencia.this, coluna);
				}
			});
			detalheColunaAcao.setActionListener(e -> {
				Coluna coluna = ((OrdenacaoModelo) getModel()).getColuna(indiceColuna);
				if (coluna != null) {
					Util.mensagem(TabelaPersistencia.this, coluna.getDetalhe());
				}
			});
			larguraTituloAcao.setActionListener(e -> larguraTitulo(indiceColuna, larguraColuna));
			larguraColunaAcao.setActionListener(e -> larguraColuna(indiceColuna));
			larguraMinimaAcao.setActionListener(e -> larguraMinima(indiceColuna));
		}

		private void preShow(String chave) {
			FontMetrics fontMetrics = getFontMetrics(getFont());
			larguraColuna = fontMetrics.stringWidth(chave) + Constantes.TRINTA;
			menuIN.setText("AND IN - " + chave);
			limparMenuChaveamento();
			List<String> lista = getChaveamento().get(chave);
			if (lista != null) {
				for (String coluna : lista) {
					add(new MenuItemChaveamento(coluna));
				}
			}
			limparItemMapeamento();
			String valorChave = getMapeamento().get(chave.toLowerCase());
			if (valorChave != null) {
				itemMapeamento.setText(valorChave);
				add(separator);
				add(itemMapeamento);
			}
		}

		private class MenuCopiarLinhas extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;

			private MenuCopiarLinhas() {
				super(TabelaMensagens.getString("label.copiar_header"), false, null);
				semAspasAcao.setActionListener(e -> copiar(false));
				comAspasAcao.setActionListener(e -> copiar(true));
			}

			private void copiar(boolean aspas) {
				SeparadorDialogo.criar(TabelaPersistencia.this, "Copiar", TabelaPersistencia.this, indiceColuna, aspas);
			}
		}

		private class MenuColocarColuna extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;
			private Action comAspasAtalhoAcao = actionMenu("label.com_aspas_atalho", Icones.ASPAS);
			private Action semAspasAtalhoAcao = actionMenu("label.sem_aspas_atalho");
			private final boolean numeros;
			private final boolean letras;

			private MenuColocarColuna(String titulo, boolean numero, boolean letra) {
				super(TabelaMensagens.getString(titulo), false, null);
				addMenuItem(true, semAspasAtalhoAcao);
				addMenuItem(comAspasAtalhoAcao);
				numeros = numero;
				letras = letra;
				semAspasAtalhoAcao.setActionListener(e -> copiar(false, true));
				comAspasAtalhoAcao.setActionListener(e -> copiar(true, true));
				semAspasAcao.setActionListener(e -> copiar(false, false));
				comAspasAcao.setActionListener(e -> copiar(true, false));
			}

			private void copiar(boolean aspas, boolean atalho) {
				if (listener != null) {
					String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
					String memoria = Util.getContentTransfered();
					memoria = Util.getString(memoria, numeros, letras);
					if (aspas && !Util.estaVazio(memoria)) {
						memoria = Util.citar(memoria);
					}
					if (atalho) {
						listener.colocarColunaComMemoriaAtalho(TabelaPersistencia.this, coluna, memoria);
					} else {
						listener.colocarColunaComMemoria(TabelaPersistencia.this, coluna, memoria);
					}
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
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				String complemento = Util.getStringLista(lista, ", ", false, aspas);
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
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				String complemento = Util.getStringLista(lista, ", ", false, aspas);
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