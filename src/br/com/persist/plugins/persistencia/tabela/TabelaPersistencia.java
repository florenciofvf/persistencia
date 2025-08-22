package br.com.persist.plugins.persistencia.tabela;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.TransferidorTabular;
import br.com.persist.assistencia.Util;
import br.com.persist.assistencia.Valor;
import br.com.persist.componente.Action;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.MenuPadrao2;
import br.com.persist.componente.MenuPadrao3;
import br.com.persist.componente.Popup;
import br.com.persist.componente.SeparadorDialogo;
import br.com.persist.componente.TextEditor;
import br.com.persist.plugins.mapeamento.Mapeamento;
import br.com.persist.plugins.mapeamento.MapeamentoProvedor;
import br.com.persist.plugins.objeto.ObjetoException;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.PersistenciaModelo;

public class TabelaPersistencia extends JTable {
	private transient TabelaPersistenciaListener listener;
	private PopupHeader popupHeader = new PopupHeader();
	private static final long serialVersionUID = 1L;
	private Map<String, List<String>> chaveamento;
	private Map<String, String> mapeamento;
	private OrdenacaoModelo modeloBackup;
	private String classBiblio;
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
		if (dataModel instanceof OrdenacaoModelo) {
			super.setModel(dataModel);
		}
	}

	public OrdenacaoModelo getModeloBackup() {
		return modeloBackup;
	}

	public void setModeloBackup(OrdenacaoModelo modeloBackup) {
		this.modeloBackup = modeloBackup;
	}

	public String getNomeColunas(String apelido) {
		return getModelo().getNomeColunas(apelido);
	}

	public List<String> getListaNomeColunasObrigatorias() {
		return getModelo().getListaNomeColunasObrigatorias();
	}

	public List<String> getListaNomeColunasPreenchidas(boolean comChaves, int indice) {
		return getModelo().getListaNomeColunasPreenchidas(comChaves, indice);
	}

	public boolean contemCampoVazio(boolean comChaves, int indice) {
		return getModelo().contemCampoVazio(comChaves, indice);
	}

	public List<String> getListaNomeColunas(boolean comChaves) {
		return getModelo().getListaNomeColunas(comChaves);
	}

	public List<Coluna> getColunas(List<String> nomes) {
		return getModelo().getColunas(nomes);
	}

	public Coluna getColuna(String nome) {
		return getModelo().getColuna(nome);
	}

	public String detalhesExcecao() {
		return getModelo().detalhesExcecao();
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

	public String getClassBiblio() {
		return classBiblio;
	}

	public void setClassBiblio(String classBiblio) {
		this.classBiblio = classBiblio;
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

	public void destacarColuna(int coluna, boolean inverter) {
		TableColumn tableColumn = getTableColumn(coluna);
		CabecalhoColuna cabecalho = (CabecalhoColuna) tableColumn.getHeaderRenderer();
		if (cabecalho != null) {
			destacar(cabecalho, inverter);
			SwingUtilities.updateComponentTreeUI(this);
			tornarVisivel(0, coluna);
		}
	}

	public void inativarColuna(int coluna, boolean inativo) {
		TableColumn tableColumn = getTableColumn(coluna);
		CabecalhoColuna cabecalho = (CabecalhoColuna) tableColumn.getHeaderRenderer();
		if (cabecalho != null) {
			inativar(cabecalho, inativo);
			SwingUtilities.updateComponentTreeUI(this);
			tornarVisivel(0, coluna);
		}
	}

	private void destacar(CabecalhoColuna cabecalho, boolean inverter) {
		if (!inverter) {
			cabecalho.setForeground(Color.WHITE);
			cabecalho.setBackground(Color.BLUE);
			return;
		}
		if (cabecalho.getBackground() == Color.BLUE) {
			cabecalho.setForeground(null);
			cabecalho.setBackground(null);
		} else {
			destacar(cabecalho, false);
		}
	}

	private void inativar(CabecalhoColuna cabecalho, boolean inativo) {
		if (inativo) {
			cabecalho.setForeground(Color.WHITE);
			cabecalho.setBackground(Color.BLACK);
		} else {
			cabecalho.setForeground(null);
			cabecalho.setBackground(null);
		}
	}

	public void deslocarColuna(String string) {
		if (Util.isEmpty(string)) {
			return;
		}
		TableColumnModel columnModel = getColumnModel();
		List<TableColumn> lista = new ArrayList<>();
		String[] strings = string.split(",");
		for (String str : strings) {
			if (!Util.isEmpty(str)) {
				TableColumn tableColumn = getTableColumn(columnModel, str.trim());
				if (tableColumn != null) {
					columnModel.removeColumn(tableColumn);
					lista.add(tableColumn);
				}
			}
		}
		for (TableColumn tableColumn : lista) {
			columnModel.addColumn(tableColumn);
		}
	}

	private TableColumn getTableColumn(TableColumnModel columnModel, String nome) {
		Enumeration<TableColumn> columns = columnModel.getColumns();
		while (columns.hasMoreElements()) {
			TableColumn element = columns.nextElement();
			CabecalhoColuna cabecalho = (CabecalhoColuna) element.getHeaderRenderer();
			if (cabecalho != null && nome.equalsIgnoreCase(cabecalho.getNome())) {
				return element;
			}
		}
		return null;
	}

	public List<String> getListaNomeColunasDestacadas() {
		List<String> lista = new ArrayList<>();
		TableColumnModel columnModel = getColumnModel();
		Enumeration<TableColumn> columns = columnModel.getColumns();
		while (columns.hasMoreElements()) {
			TableColumn element = columns.nextElement();
			CabecalhoColuna cabecalho = (CabecalhoColuna) element.getHeaderRenderer();
			if (cabecalho != null && cabecalho.getBackground() == Color.BLUE) {
				lista.add(cabecalho.getColuna().getNome());
			}
		}
		return lista;
	}

	public void larguraColuna(int coluna) {
		TableColumn tableColumn = getTableColumn(coluna);
		int atual = tableColumn.getWidth();
		Object resp = Util.getValorInputDialog(TabelaPersistencia.this, "label.largura_manual", "" + atual, "" + atual);
		if (resp == null || Util.isEmpty(resp.toString())) {
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

	private class PopupHeader extends Popup {
		private JCheckBoxMenuItem inativoTempCheck = new JCheckBoxMenuItem(getString("label.inativo_temp"));
		private Action pesquisaApartirColunaAcao = acaoMenu("label.pesquisa_a_partir_coluna");
		private Action mapearApartirBiblioAcao = acaoMenu("label.mapear_a_partir_biblio");
		private Action copiarNomeColunaAcao = acaoMenu("label.copiar_nome_coluna");
		private transient ProcessarTitulo processarTitulo = new ProcessarTitulo();
		private Action larguraConteudoAcao = acaoMenu("label.largura_conteudo");
		private Action larguraColunaAcao = actionMenu("label.largura_manual");
		private Action larguraTituloAcao = acaoMenu("label.largura_titulo");
		private Action larguraMinimaAcao = acaoMenu("label.largura_minima");
		private MenuAbrirArquivo menuAbrirArquivo = new MenuAbrirArquivo();
		private MenuCopiarLinhas menuCopiarLinhas = new MenuCopiarLinhas();
		private MenuExibirLinhas menuExibirLinhas = new MenuExibirLinhas();
		private Action destacarColunaAcao = actionMenu("label.destacar");
		private ItemClassBiblio itemClassBiblio = new ItemClassBiblio();
		private ItemMapeamento itemMapeamento = new ItemMapeamento();
		private MenuMetadados menuMetadados = new MenuMetadados();
		private Separator separatorChave = new Separator();
		private Separator separatorInfo = new Separator();
		private static final long serialVersionUID = 1L;
		private static final String AND = "AND ";
		private transient Coluna colunaTabela;
		private MenuIN menuIN = new MenuIN();
		private int larguraColuna;
		private int indiceColuna;

		private PopupHeader() {
			add(menuMetadados);
			add(true, inativoTempCheck);
			addMenuItem(larguraConteudoAcao);
			addMenuItem(larguraTituloAcao);
			addMenuItem(larguraMinimaAcao);
			addMenuItem(larguraColunaAcao);
			addMenuItem(destacarColunaAcao, getString("hint.destacar"));
			addMenuItem(true, pesquisaApartirColunaAcao);
			addMenuItem(true, mapearApartirBiblioAcao);
			add(true, new MenuColocarNomeColuna());
			addMenuItem(copiarNomeColunaAcao);
			add(true, new MenuColocarColuna("label.copiar_nome_coluna_concat_n", true, false));
			add(new MenuColocarColuna("label.copiar_nome_coluna_concat_l", false, true));
			add(new MenuColocarColuna("label.copiar_nome_coluna_concat_t", false, false));
			add(true, menuCopiarLinhas);
			add(menuExibirLinhas);
			add(menuAbrirArquivo);
			add(true, menuIN);
			eventos();
		}

		Action acaoMenu(String chave, Icon icon) {
			return Action.acaoMenu(getString(chave), icon);
		}

		Action acaoMenu(String chave) {
			return acaoMenu(chave, null);
		}

		String getString(String chave) {
			return TabelaMensagens.getString(chave);
		}

		private class MenuMetadados extends Menu {
			private MenuTotalLengthString2 menorLengthString = new MenuTotalLengthString2();
			private MenuTotalLengthString maiorLengthString = new MenuTotalLengthString();
			private Action exportaParaAcao = acaoMenu("label.campo_exportado_para");
			private MenuTotalQueRepetem totalQueRepetem = new MenuTotalQueRepetem();
			private MenuTotalRepetidos totalRepetidos = new MenuTotalRepetidos();
			private MenuRepetidoComQtd repetidoComQtd = new MenuRepetidoComQtd();
			private Action importaDeAcao = acaoMenu("label.campo_importado_de");
			private MenuSelectDistinct distinct = new MenuSelectDistinct();
			private MenuSelectGroupBy groupBy = new MenuSelectGroupBy();
			private Action infoColunaAcao = actionMenu("label.info");
			private MenuMinimo minimo = new MenuMinimo();
			private MenuMaximo maximo = new MenuMaximo();
			private static final long serialVersionUID = 1L;

			private MenuMetadados() {
				super(Constantes.LABEL_METADADOS, Icones.INFO);
				addMenuItem(infoColunaAcao);
				addMenuItem(true, exportaParaAcao);
				addMenuItem(true, importaDeAcao);
				addSeparator();
				add(totalQueRepetem);
				add(totalRepetidos);
				add(maiorLengthString);
				add(menorLengthString);
				add(repetidoComQtd);
				addSeparator();
				add(distinct);
				add(groupBy);
				addSeparator();
				add(minimo);
				add(maximo);
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
					List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
					StringBuilder builder = new StringBuilder(coluna.getDetalhe());
					if (!lista.isEmpty()) {
						builder.append("CARACTERES: " + Constantes.QL);
						for (String string : lista) {
							builder.append(string.length() + Constantes.QL);
						}
					}
					Util.mensagem(TabelaPersistencia.this, builder.toString());
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

			private class MenuTotalQueRepetem extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuTotalQueRepetem() {
					super(TabelaMensagens.getString("label.total_valores_que_repetem"), false, null);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectTotalValoresQueRepetem(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}

			private class MenuTotalRepetidos extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuTotalRepetidos() {
					super(TabelaMensagens.getString("label.total_valor_mais_repetido"), false, null);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectTotalValorMaisRepetido(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}

			private class MenuTotalLengthString extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuTotalLengthString() {
					super(TabelaMensagens.getString("label.total_maior_length_string"), false, null);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectTotalMaiorLengthString(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}

			private class MenuTotalLengthString2 extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuTotalLengthString2() {
					super(TabelaMensagens.getString("label.total_menor_length_string"), false, null);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectTotalMenorLengthString(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}

			private class MenuRepetidoComQtd extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuRepetidoComQtd() {
					super(TabelaMensagens.getString("label.valor_repetido_com_qtd"), false, null);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectValorRepetidoComSuaQtd(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}

			private class MenuSelectDistinct extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuSelectDistinct() {
					super(TabelaMensagens.getString("label.select_distinct"), false, null);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectDistinct(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}

			private class MenuSelectGroupBy extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuSelectGroupBy() {
					super(TabelaMensagens.getString("label.select_group_by"), false, null);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectGroupBy(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}

			private class MenuMinimo extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuMinimo() {
					super("label.minimo", Icones.VAR);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectMinimo(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}

			private class MenuMaximo extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuMaximo() {
					super("label.maximo", Icones.VAR);
					formularioAcao.setActionListener(e -> abrirSelect(true));
					dialogoAcao.setActionListener(e -> abrirSelect(false));
				}

				private void abrirSelect(boolean abrirEmForm) {
					if (listener != null) {
						String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
						listener.selectMaximo(TabelaPersistencia.this, coluna, abrirEmForm);
					}
				}
			}
		}

		private void eventos() {
			inativoTempCheck.addActionListener(e -> {
				if (colunaTabela != null) {
					colunaTabela.setInativoTemp(inativoTempCheck.isSelected());
					inativarColuna(colunaTabela.getIndice(), colunaTabela.isInativoTemp());
				}
			});
			copiarNomeColunaAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(indiceColuna);
				Util.setContentTransfered(coluna);
			});
			pesquisaApartirColunaAcao.setActionListener(e -> {
				String coluna = getModel().getColumnName(indiceColuna);
				if (listener != null) {
					try {
						listener.pesquisaApartirColuna(TabelaPersistencia.this, coluna);
					} catch (ObjetoException ex) {
						Util.mensagem(TabelaPersistencia.this, ex.getMessage());
					}
				}
			});
			mapearApartirBiblioAcao.setActionListener(e -> {
				if (!Util.isEmpty(classBiblio) && itemClassBiblio.isVisible()) {
					itemClassBiblio.doClick();
				} else if (listener != null && colunaTabela != null) {
					listener.mapearApartirBiblio(TabelaPersistencia.this, colunaTabela);
				}
			});
			larguraTituloAcao.setActionListener(e -> larguraTitulo(indiceColuna, larguraColuna));
			destacarColunaAcao.setActionListener(e -> destacarColuna(indiceColuna, true));
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
				if (obj != null && !Util.isEmpty(obj.toString())) {
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
			String valor = getMapeamento().get(chave.toLowerCase());
			if (!Util.isEmpty(valor) || !Util.isEmpty(classBiblio)) {
				add(separatorInfo);
			}
			if (!Util.isEmpty(valor)) {
				itemMapeamento.setText(valor);
				add(itemMapeamento);
			}
			if (!Util.isEmpty(classBiblio)) {
				itemClassBiblio.setToolTipText(classBiblio);
				itemClassBiblio.setText(getNome(classBiblio));
				add(itemClassBiblio);
			}
			this.colunaTabela = colunaTabela;
			if (colunaTabela != null) {
				inativoTempCheck.setSelected(colunaTabela.isInativoTemp());
			}
		}

		private String getNome(String classBiblio) {
			int pos = classBiblio.lastIndexOf('.');
			return pos == -1 ? classBiblio : classBiblio.substring(pos + 1);
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
				if (!Util.isEmpty(titulo)) {
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
				if (!Util.isEmpty(titulo)) {
					return titulo.startsWith(AND) ? AND : Constantes.VAZIO;
				}
				return Constantes.VAZIO;
			}
		}

		private class MenuColocarNomeColuna extends Menu {
			private Action atalhoAcao = acaoMenu("label.atalho");
			private Action opcoesAcao = acaoMenu("label.opcoes");
			private static final long serialVersionUID = 1L;

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
			private Action comAspasAtalhoAcao = acaoMenu("label.com_aspas_atalho", Icones.ASPAS);
			private Action destacComBarraAcao = acaoMenu("label.copiar_destac_com_barra");
			private Action semAspasAtalhoAcao = acaoMenu("label.sem_aspas_atalho");
			private static final long serialVersionUID = 1L;

			private MenuCopiarLinhas() {
				super(TabelaMensagens.getString("label.copiar_header"), false, null);
				addMenuItem(true, destacComBarraAcao);
				addMenuItem(true, semAspasAtalhoAcao);
				addMenuItem(comAspasAtalhoAcao);
				destacComBarraAcao.setActionListener(e -> copiarDestacComBarra());
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
				if (!Util.isEmpty(string)) {
					Util.setContentTransfered(string);
					setIcon(Icones.SUCESSO);
				}
			}

			private void copiarDestacComBarra() {
				List<Integer> indices = Util.getIndicesLinha(TabelaPersistencia.this);
				TransferidorTabular transferidor = Util.criarTransferidorTabular(TabelaPersistencia.this,
						getListaNomeColunasDestacadas(), indices);
				if (transferidor != null) {
					Util.setContentTransfered(transferidor.getBarra());
				}
			}
		}

		private class MenuExibirLinhas extends Menu {
			private Action msgPadraoAcao = acaoMenu("label.msg_padrao");
			private Action msgOptionAcao = acaoMenu("label.msg_option");
			private static final long serialVersionUID = 1L;

			private MenuExibirLinhas() {
				super(TabelaMensagens.getString("label.exibir_valores"), false, null);
				addMenuItem(msgPadraoAcao);
				addMenuItem(msgOptionAcao);
				msgPadraoAcao.setActionListener(e -> exibirValores(false));
				msgOptionAcao.setActionListener(e -> exibirValores(true));
			}

			private void exibirValores(boolean option) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				String string = Util.getStringLista(lista, Constantes.QL2, true, false);
				boolean paintERT = TextEditor.isPaintERT();
				if (option) {
					Util.setMensagemHtml(true);
					string = Util.getHtml(string);
					TextEditor.setPaintERT(false);
				}
				Util.mensagem(TabelaPersistencia.this, string);
				TextEditor.setPaintERT(paintERT);
			}
		}

		private class MenuAbrirArquivo extends Menu {
			private Action diretorioAcao = acaoMenu("label.diretorio");
			private Action abrirAcao = acaoMenu("label.abrir");
			private static final long serialVersionUID = 1L;

			private MenuAbrirArquivo() {
				super(TabelaMensagens.getString("label.abrir_arquivos"), false, null);
				addMenuItem(diretorioAcao);
				addMenuItem(abrirAcao);
				diretorioAcao.setActionListener(e -> abrir(true));
				abrirAcao.setActionListener(e -> abrir(false));
			}

			private void abrir(boolean diretorio) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				if (diretorio) {
					Util.diretorio(TabelaPersistencia.this, lista);
				} else {
					Util.abrir(TabelaPersistencia.this, lista);
				}
			}
		}

		private class MenuColocarColuna extends MenuPadrao2 {
			private Action comAspasAtalhoAcao = acaoMenu("label.com_aspas_atalho2", Icones.ASPAS);
			private Action semAspasAtalhoAcao = acaoMenu("label.sem_aspas_atalho2");
			private static final long serialVersionUID = 1L;
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
					if (aspas && !Util.isEmpty(memoria)) {
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
			private JCheckBoxMenuItem chkConcatTransf = new JCheckBoxMenuItem(
					TabelaMensagens.getString("label.concat_transfer"));

			private MenuIN() {
				super(Constantes.LABEL_VAZIO);
				addItem(chkConcatTransf);
				semAspasAcao.setActionListener(e -> copiarIN(false));
				comAspasAcao.setActionListener(e -> copiarIN(true));
				addMouseListener(processarTitulo);
			}

			private void copiarIN(boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				String complemento = Util.getStringLista(lista, ", ", false, aspas);
				if (!Util.isEmpty(complemento)) {
					String coluna = TabelaPersistencia.this.getModel().getColumnName(indiceColuna);
					String string = Constantes.VAZIO;
					if (chkConcatTransf.isSelected()) {
						String str = Util.getContentTransfered();
						if (!Util.isEmpty(str)) {
							string = str + " ";
						}
					}
					Util.setContentTransfered(
							string + processarTitulo.get(MenuIN.this) + coluna + " IN (" + complemento + ")");
				} else {
					Util.setContentTransfered(" ");
				}
			}
		}

		private class MenuItemChaveamento extends MenuPadrao2 {
			private static final long serialVersionUID = 1L;
			private JCheckBoxMenuItem chkConcatTransf = new JCheckBoxMenuItem(
					TabelaMensagens.getString("label.concat_transfer"));
			private final String nomeColuna;

			private MenuItemChaveamento(String coluna) {
				super(Constantes.LABEL_VAZIO);
				addItem(chkConcatTransf);
				setText(AND + coluna + " IN");
				this.nomeColuna = coluna;
				semAspasAcao.setActionListener(e -> copiarINDinamico(false));
				comAspasAcao.setActionListener(e -> copiarINDinamico(true));
				addMouseListener(processarTitulo);
			}

			private void copiarINDinamico(boolean aspas) {
				List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this, indiceColuna);
				String complemento = Util.getStringLista(lista, ", ", false, aspas);
				if (!Util.isEmpty(complemento)) {
					String string = Constantes.VAZIO;
					if (chkConcatTransf.isSelected()) {
						String str = Util.getContentTransfered();
						if (!Util.isEmpty(str)) {
							string = str + " ";
						}
					}
					Util.setContentTransfered(string + processarTitulo.get(MenuItemChaveamento.this) + nomeColuna
							+ " IN (" + complemento + ")");
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
			remove(itemClassBiblio);
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

		private class ItemClassBiblio extends MenuItem {
			private static final long serialVersionUID = 1L;

			private ItemClassBiblio() {
				super("label.info");
				addActionListener(e -> {
					Class<?> classe = null;
					try {
						classe = Class.forName(getToolTipText());
					} catch (ClassNotFoundException ex) {
						String msg = TabelaMensagens.getString("msg.class_biblio");
						Util.mensagem(TabelaPersistencia.this, msg + ex.getMessage());
						return;
					}
					Field field = TabelaPersistenciaUtil.getFieldParaColuna(classe, colunaTabela.getNome());
					if (field == null) {
						String msg = TabelaMensagens.getString("msg.class_biblio_field_inexist",
								colunaTabela.getNome());
						Util.mensagem(TabelaPersistencia.this, msg);
						return;
					}
					try {
						List<String> lista = TabelaPersistenciaUtil.getValoresLinha(TabelaPersistencia.this,
								indiceColuna);
						Set<String> set = new HashSet<>(lista);
						List<String> selecionados = new ArrayList<>();
						String string = TabelaPersistenciaUtil.descreverField(field, criarListaValores(set),
								selecionados);
						String sel = selecionados.size() == 1 ? selecionados.get(0) : null;
						Util.mensagemSel(TabelaPersistencia.this, string, sel);
					} catch (Exception ex) {
						Util.mensagem(TabelaPersistencia.this, ex.getMessage());
					}
				});
			}

			private List<Valor> criarListaValores(Set<String> set) {
				List<Valor> resp = new ArrayList<>();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					resp.add(new Valor(it.next().trim()));
				}
				return resp;
			}
		}
	}

	public void configClasseBiblio(String classe, Coluna coluna) {
		setClassBiblio(classe);
		popupHeader.preShow(coluna.getNome(), coluna);
		popupHeader.itemClassBiblio.doClick();
	}
}