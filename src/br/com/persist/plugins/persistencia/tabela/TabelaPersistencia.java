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
import br.com.persist.componente.Menu;
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

	public List<String> getListaNomeColunasObrigatorias() {
		return getModelo().getListaNomeColunasObrigatorias();
	}

	public List<String> getListaNomeColunas(boolean comChaves) {
		return getModelo().getListaNomeColunas(comChaves);
	}

	public OrdenacaoModelo getModelo() {
		return (OrdenacaoModelo) getModel();
	}

	public void atualizarSequencias(Map<String, String> mapaSequencia) {
		getModelo().atualizarSequencias(mapaSequencia);
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
				TableColumnModel columnModel = getColumnModel();
				TableColumn tableColumn = columnModel.getColumn(tableColuna);
				CabecalhoColuna cabecalho = (CabecalhoColuna) tableColumn.getHeaderRenderer();
				popupHeader.preShow(getModel().getColumnName(modelColuna), cabecalho.getColuna());
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

				boolean shift = e.isShiftDown();
				boolean alt = e.isAltDown();
				boolean ctrl = alt && shift;
				if ((ctrl || shift) && listener != null) {
					boolean concat = shift;
					if (ctrl) {
						concat = false;
					}
					listener.colocarNomeColunaAtalho(TabelaPersistencia.this, cabecalho.getNome(), concat,
							cabecalho.getColuna());
					return;
				}

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
		Object resp = Util.getValorInputDialog(TabelaPersistencia.this, "label.largura_manual", "" + atual, "" + atual);
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
		private Action pesquisaApartirColunaAcao = actionMenu("label.pesquisa_a_partir_coluna");
		private Action larguraColunaAcao = Action.actionMenu("label.largura_manual", null);
		private Action copiarNomeColunaAcao = actionMenu("label.copiar_nome_coluna");
		private transient ProcessarTitulo processarTitulo = new ProcessarTitulo();
		private Action larguraConteudoAcao = actionMenu("label.largura_conteudo");
		private Action larguraTituloAcao = actionMenu("label.largura_titulo");
		private Action larguraMinimaAcao = actionMenu("label.largura_minima");
		private MenuCopiarLinhas menuCopiarLinhas = new MenuCopiarLinhas();
		private ItemMapeamento itemMapeamento = new ItemMapeamento();
		private MenuMetadados menuMetadados = new MenuMetadados();
		private Separator separatorChave = new Separator();
		private Separator separatorInfo = new Separator();
		private static final String AND = "AND ";
		private transient Coluna colunaTabela;
		private MenuIN menuIN = new MenuIN();
		private int larguraColuna;
		private int indiceColuna;

		private PopupHeader() {
			add(menuMetadados);
			addMenuItem(true, larguraConteudoAcao);
			addMenuItem(larguraTituloAcao);
			addMenuItem(larguraMinimaAcao);
			addMenuItem(larguraColunaAcao);
			addMenuItem(true, pesquisaApartirColunaAcao);
			add(true, new MenuColocarNomeColuna());
			addMenuItem(copiarNomeColunaAcao);
			add(true, new MenuColocarColuna("label.copiar_nome_coluna_concat_n", true, false));
			add(new MenuColocarColuna("label.copiar_nome_coluna_concat_l", false, true));
			add(new MenuColocarColuna("label.copiar_nome_coluna_concat_t", false, false));
			add(true, menuCopiarLinhas);
			add(true, menuIN);
			eventos();
		}

		private class MenuMetadados extends Menu {
			private static final long serialVersionUID = 1L;
			private Action exportaParaAcao = actionMenu("label.campo_exportado_para");
			private Action importaDeAcao = actionMenu("label.campo_importado_de");
			private Action infoColunaAcao = Action.actionMenu("label.info", null);

			private MenuMetadados() {
				super(Constantes.LABEL_METADADOS, Icones.INFO);
				addMenuItem(infoColunaAcao);
				addMenuItem(true, exportaParaAcao);
				addMenuItem(true, importaDeAcao);
				exportaParaAcao.setActionListener(e -> importarExportarInfo(true));
				importaDeAcao.setActionListener(e -> importarExportarInfo(false));
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						infoColuna();
					}
				});
				infoColunaAcao.setActionListener(e -> infoColuna());
			}

			private void infoColuna() {
				Coluna coluna = ((OrdenacaoModelo) TabelaPersistencia.this.getModel()).getColuna(indiceColuna);
				if (coluna != null) {
					Util.mensagem(TabelaPersistencia.this, coluna.getDetalhe());
				}
			}

			private void importarExportarInfo(boolean exportar) {
				if (listener != null) {
					String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
					if (exportar) {
						listener.campoExportadoPara(coluna);
					} else {
						listener.campoImportadoDe(coluna);
					}
				}
			}
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
			larguraTituloAcao.setActionListener(e -> larguraTitulo(indiceColuna, larguraColuna));
			larguraConteudoAcao.setActionListener(e -> larguraConteudo(indiceColuna));
			larguraColunaAcao.setActionListener(e -> larguraColuna(indiceColuna));
			larguraMinimaAcao.setActionListener(e -> larguraMinima(indiceColuna));
		}

		private void larguraConteudo(int coluna) {
			FontMetrics fontMetrics = getFontMetrics(getFont());
			TableColumn tableColumn = getTableColumn(coluna);
			TableModel model = getModel();
			String chave = model.getColumnName(coluna);
			int maior = fontMetrics.stringWidth(chave) + Constantes.TRINTA;
			for (int i = 0; i < model.getRowCount(); i++) {
				Object obj = model.getValueAt(i, coluna);
				if (obj != null && !Util.estaVazio(obj.toString())) {
					int valor = fontMetrics.stringWidth(obj.toString()) + Constantes.TRINTA;
					if (valor > maior) {
						maior = valor;
					}
				}
			}
			tableColumn.setPreferredWidth(maior);
		}

		private void larguraMinima(int coluna) {
			TableColumn tableColumn = getTableColumn(coluna);
			tableColumn.setPreferredWidth(Constantes.DEZ);
		}

		private void preShow(String chave, Coluna colunaTabela) {
			FontMetrics fontMetrics = getFontMetrics(getFont());
			larguraColuna = fontMetrics.stringWidth(chave) + Constantes.TRINTA;
			menuIN.setText(AND + chave + " IN");
			menuCopiarLinhas.setIcon(null);
			limparMenuChaveamento();
			List<String> lista = getChaveamento().get(chave.toLowerCase());
			if (lista != null && !lista.isEmpty()) {
				add(separatorChave);
				for (String coluna : lista) {
					add(new MenuItemChaveamento(coluna));
				}
			}
			limparItemMapeamento();
			String valorChave = getMapeamento().get(chave.toLowerCase());
			if (valorChave != null) {
				itemMapeamento.setText(valorChave);
				add(separatorInfo);
				add(itemMapeamento);
			}
			this.colunaTabela = colunaTabela;
		}

		private class ProcessarTitulo extends MouseAdapter {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() instanceof Menu) {
					Menu menu = (Menu) e.getSource();
					processar(menu);
				}
			}

			private void processar(Menu menu) {
				String titulo = menu.getText();
				if (!Util.estaVazio(titulo)) {
					if (titulo.startsWith(AND)) {
						titulo = titulo.substring(AND.length());
					} else {
						titulo = AND + titulo;
					}
					menu.setText(titulo);
				}
			}

			private String get(Menu menu) {
				String titulo = menu.getText();
				if (!Util.estaVazio(titulo)) {
					return titulo.startsWith(AND) ? AND : Constantes.VAZIO;
				}
				return Constantes.VAZIO;
			}
		}

		private class MenuColocarNomeColuna extends Menu {
			private static final long serialVersionUID = 1L;
			private Action atalhoAcao = actionMenu("label.atalho");
			private Action opcoesAcao = actionMenu("label.opcoes");

			private MenuColocarNomeColuna() {
				super(TabelaMensagens.getString("label.colocar_nome_coluna"), false, null);
				addMenuItem(opcoesAcao);
				addMenuItem(true, atalhoAcao);
				opcoesAcao.setActionListener(e -> colocarNomeColuna(false));
				atalhoAcao.setActionListener(e -> colocarNomeColuna(true));
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						colocarNomeColuna(true);
					}
				});
			}

			private void colocarNomeColuna(boolean atalho) {
				if (listener != null) {
					String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
					if (atalho) {
						listener.colocarNomeColunaAtalho(TabelaPersistencia.this, coluna, false, colunaTabela);
					} else {
						listener.colocarNomeColuna(TabelaPersistencia.this, coluna, colunaTabela);
					}
				}
			}
		}

		private class MenuCopiarLinhas extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;
			private Action comAspasAtalhoAcao = actionMenu("label.com_aspas_atalho", Icones.ASPAS);
			private Action semAspasAtalhoAcao = actionMenu("label.sem_aspas_atalho");

			private MenuCopiarLinhas() {
				super(TabelaMensagens.getString("label.copiar_header"), false, null);
				addMenuItem(true, semAspasAtalhoAcao);
				addMenuItem(comAspasAtalhoAcao);
				semAspasAtalhoAcao.setActionListener(e -> copiarAtalho(false));
				comAspasAtalhoAcao.setActionListener(e -> copiarAtalho(true));
				semAspasAcao.setActionListener(e -> copiar(false));
				comAspasAcao.setActionListener(e -> copiar(true));
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						copiarAtalho(true);
					}
				});
			}

			private void copiar(boolean aspas) {
				SeparadorDialogo.criar(TabelaPersistencia.this, "Copiar", TabelaPersistencia.this, indiceColuna, aspas,
						null);
			}

			private void copiarAtalho(boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				String string = Util.getStringLista(lista, ", ", false, aspas);
				if (!Util.estaVazio(string)) {
					Util.setContentTransfered(string);
					setIcon(Icones.SUCESSO);
				}
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
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						copiar(true, true);
					}
				});
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
				addMouseListener(processarTitulo);
			}

			private void copiarIN(boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				String complemento = Util.getStringLista(lista, ", ", false, aspas);
				if (!Util.estaVazio(complemento)) {
					String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
					Util.setContentTransfered(processarTitulo.get(MenuIN.this) + coluna + " IN (" + complemento + ")");
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
				setText(AND + coluna + " IN");
				this.nomeColuna = coluna;
				semAspasAcao.setActionListener(e -> copiarINDinamico(false));
				comAspasAcao.setActionListener(e -> copiarINDinamico(true));
				addMouseListener(processarTitulo);
			}

			private void copiarINDinamico(boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				String complemento = Util.getStringLista(lista, ", ", false, aspas);
				if (!Util.estaVazio(complemento)) {
					Util.setContentTransfered(
							processarTitulo.get(MenuItemChaveamento.this) + nomeColuna + " IN (" + complemento + ")");
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
			remove(separatorChave);
			MenuItemChaveamento menu = getPrimeiroMenuItemChaveamento();
			while (menu != null) {
				remove(menu);
				menu = getPrimeiroMenuItemChaveamento();
			}
		}

		private void limparItemMapeamento() {
			remove(separatorInfo);
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