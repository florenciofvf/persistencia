package br.com.persist.fichario;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;

import br.com.persist.anexo.AnexoTreeContainer;
import br.com.persist.anexo.AnexoTreeFormulario;
import br.com.persist.anotacao.AnotacaoContainer;
import br.com.persist.anotacao.AnotacaoFormulario;
import br.com.persist.arquivo.ArquivoTreeModelo;
import br.com.persist.arquivo.ArquivoTreeContainer;
import br.com.persist.arquivo.ArquivoTreeFormulario;
import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.comparacao.ComparacaoContainer;
import br.com.persist.comparacao.ComparacaoFormulario;
import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoContainer;
import br.com.persist.conexao.ConexaoFormulario;
import br.com.persist.configuracao.ConfiguracaoContainer;
import br.com.persist.configuracao.ConfiguracaoFormulario;
import br.com.persist.consulta.ConsultaContainer;
import br.com.persist.consulta.ConsultaFormulario;
import br.com.persist.container.Container;
import br.com.persist.container.ContainerFormulario;
import br.com.persist.desktop.Desktop;
import br.com.persist.desktop.DesktopFormulario;
import br.com.persist.fragmento.FragmentoContainer;
import br.com.persist.fragmento.FragmentoFormulario;
import br.com.persist.icone.Icones;
import br.com.persist.mapeamento.MapeamentoContainer;
import br.com.persist.mapeamento.MapeamentoFormulario;
import br.com.persist.metadado.Metadado;
import br.com.persist.metadado.MetadadoTreeContainer;
import br.com.persist.metadado.MetadadoTreeFormulario;
import br.com.persist.objeto.Objeto;
import br.com.persist.objeto.ObjetoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.requisicao.RequisicaoContainer;
import br.com.persist.requisicao.RequisicaoFormulario;
import br.com.persist.runtime_exec.RuntimeExecContainer;
import br.com.persist.runtime_exec.RuntimeExecFormulario;
import br.com.persist.superficie.Superficie;
import br.com.persist.update.UpdateContainer;
import br.com.persist.update.UpdateFormulario;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import br.com.persist.util.PosicaoDimensao;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.variaveis.VariaveisContainer;
import br.com.persist.variaveis.VariaveisFormulario;
import br.com.persist.variaveis.VariaveisModelo;
import br.com.persist.xml.XMLColetor;

public class Fichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private final transient NavegButton navegButtonEsquerdo = new NavegButton(1);
	private final transient NavegButton navegButtonDireito = new NavegButton(2);
	private final transient NavegButton navegButtonLimpar = new NavegButton(0);
	private final transient Configuracao configuracao = new Configuracao();
	private final transient SalvarAberto salvarAberto = new SalvarAberto();
	private final transient MetadadoTree metadadoTree = new MetadadoTree();
	private final transient RuntimeExec runtimeExec = new RuntimeExec();
	private final transient ArquivoTree arquivoTree = new ArquivoTree();
	private final transient Mapeamento mapeamento = new Mapeamento();
	private final transient Comparacao comparacao = new Comparacao();
	private final transient Requisicao requisicao = new Requisicao();
	private final transient Variaveis variaveis = new Variaveis();
	private final transient Fragmento fragmento = new Fragmento();
	private final transient Conteiner conteiner = new Conteiner();
	private final transient AnexoTree anexoTree = new AnexoTree();
	private final transient NavegacaoListener navegacaoListener;
	private final transient Arquivos arquivos = new Arquivos();
	private final transient Destacar destacar = new Destacar();
	private final transient Anotacao anotacao = new Anotacao();
	private final transient Conexoes conexoes = new Conexoes();
	private final transient Consulta consulta = new Consulta();
	private final transient Listener listener = new Listener();
	private final transient Desktops desktops = new Desktops();
	private final transient Objetos objetos = new Objetos();
	private final transient Update update = new Update();
	private static final Logger LOG = Logger.getGlobal();
	private transient Ponto ponto;
	private Rectangle rectangle;
	private int ultX;
	private int ultY;

	public Fichario() {
		navegacaoListener = new NavegacaoListener();
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		new DropTarget(this, listenerSoltar);
		addMouseMotionListener(listener);
		addMouseListener(listener);
		add(navegButtonLimpar);
		add(navegButtonEsquerdo);
		add(navegButtonDireito);
		config();
	}

	public void ativarNavegacao() {
		navegacaoListener.inicializacao();
		removeChangeListener(navegacaoListener);
		addChangeListener(navegacaoListener);
	}

	public class NavegButton extends BasicArrowButton implements UIResource, SwingConstants {
		private static final long serialVersionUID = 1L;
		final int tipo;

		public NavegButton(int tipo) {
			super(tipo == 0 || tipo == 1 ? WEST : EAST, UIManager.getColor("TabbedPane.selected"),
					UIManager.getColor("TabbedPane.shadow"), UIManager.getColor("TabbedPane.darkShadow"),
					UIManager.getColor("TabbedPane.highlight"));
			addActionListener(e -> click());
			if (tipo == 0) {
				setDirection(0);
			}
			this.tipo = tipo;
		}

		private void click() {
			if (tipo == 0) {
				if (Util.confirmar(Fichario.this, "msg.confirmar_limpar")) {
					navegacaoListener.inicializacao();
				}
			} else if (tipo == 1) {
				navegacaoListener.voltar();
			} else {
				navegacaoListener.avancar();
			}
		}

		private void checarEstado() {
			if (tipo == 0) {
				setEnabled(!navegacaoListener.esquerdo.isEmpty() || !navegacaoListener.direito.isEmpty());
			} else if (tipo == 1) {
				setEnabled(!navegacaoListener.esquerdo.isEmpty());
			} else {
				setEnabled(!navegacaoListener.direito.isEmpty());
			}
		}

		@Override
		public void setBounds(int x, int y, int width, int height) {
			int xAux = -1;

			if (tipo == 0) {
				xAux = 0;
			} else if (tipo == 1) {
				xAux = 15;
			} else {
				xAux = 30;
			}

			super.setBounds(xAux, 0, 15, 10);
		}
	}

	private class NavegacaoListener implements ChangeListener {
		private final Deque<Integer> esquerdo;
		private final Deque<Integer> direito;
		private boolean habilitado;
		private Integer ultimo;

		public NavegacaoListener() {
			esquerdo = new ArrayDeque<>();
			direito = new ArrayDeque<>();
			habilitado = true;
		}

		private void checarEstadoNavegacao() {
			if (getTabCount() < 1) {
				navegacaoListener.esquerdo.clear();
				navegacaoListener.direito.clear();
			}

			navegButtonEsquerdo.checarEstado();
			navegButtonDireito.checarEstado();
			navegButtonLimpar.checarEstado();
		}

		private void inicializacao() {
			ultimo = getSelectedIndex();
			habilitado = true;
			esquerdo.clear();
			direito.clear();
			checarEstadoNavegacao();
		}

		private void push(Deque<Integer> deque, Integer i) {
			if (deque == null || i == null || i == -1) {
				return;
			}

			if (deque.isEmpty()) {
				deque.push(i);
			} else {
				Integer a = deque.peek();

				if (!a.equals(i)) {
					deque.push(i);
				}
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (habilitado) {
				direito.clear();
				push(esquerdo, ultimo);
				ultimo = getSelectedIndex();
				checarEstadoNavegacao();
			}
		}

		private void voltar() {
			if (!esquerdo.isEmpty()) {
				Integer indice = esquerdo.pop();

				if (indice < getTabCount()) {
					habilitado = false;
					Integer ult = getSelectedIndex();
					setSelectedIndex(indice);
					if (!indice.equals(ult)) {
						push(direito, ult);
					}
					habilitado = true;
				}
			}

			checarEstadoNavegacao();
		}

		private void avancar() {
			if (!direito.isEmpty()) {
				Integer indice = direito.pop();

				if (indice < getTabCount()) {
					habilitado = false;
					Integer ult = getSelectedIndex();
					setSelectedIndex(indice);
					if (!indice.equals(ult)) {
						push(esquerdo, ult);
					}
					habilitado = true;
				}
			}

			checarEstadoNavegacao();
		}
	}

	private void config() {
		inputMap().put(getKeyStroke(KeyEvent.VK_Q), "excluir_action");
		getActionMap().put("excluir_action", excluirAction);
	}

	public Component getThis() {
		return this;
	}

	private transient Action excluirAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int indice = getSelectedIndex();

			if (indice != -1) {
				remove(indice);
			}
		}
	};

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
	}

	public static KeyStroke getKeyStroke(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	private transient DropTargetListener listenerSoltar = new DropTargetListener() {
		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			e.rejectDrag();
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			e.rejectDrag();
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			Point point = dtde.getLocation();
			int indice = indexAtLocation(point.x, point.y);

			if (indice != -1 && indice != getSelectedIndex()) {
				setSelectedIndex(indice);
			}
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			LOG.log(Level.FINEST, "dragExit");
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			e.rejectDrop();
		}
	};

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (rectangle != null) {
			g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}
	}

	public class Destacar {
		public void destacar(Formulario formulario, Conexao conexao, Superficie superficie, int tipoContainer,
				ConfigArquivo config) {
			List<Objeto> lista = superficie.getSelecionados();
			boolean continua = false;

			for (Objeto objeto : lista) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					continua = true;
					break;
				}
			}

			if (!continua) {
				return;
			}

			List<Objeto> selecionados = new ArrayList<>();

			for (Objeto objeto : lista) {
				if (objeto.isCopiarDestacado()) {
					selecionados.add(objeto.clonar());
				} else {
					selecionados.add(objeto);
				}
			}

			if (tipoContainer == Constantes.TIPO_CONTAINER_FORMULARIO) {
				destacarForm(formulario, selecionados, conexao, config);

			} else if (tipoContainer == Constantes.TIPO_CONTAINER_DESKTOP) {
				destacarDesk(formulario, selecionados, conexao, config);

			} else if (tipoContainer == Constantes.TIPO_CONTAINER_FICHARIO) {
				destacarObjt(formulario, selecionados, conexao);

			} else if (tipoContainer == Constantes.TIPO_CONTAINER_PROPRIO) {
				destacarProp(formulario, selecionados, conexao, superficie, config);
			}
		}

		private void destacarForm(Formulario formulario, List<Objeto> objetos, Conexao conexao, ConfigArquivo config) {
			DesktopFormulario form = DesktopFormulario.criar(formulario);

			int x = 10;
			int y = 10;

			for (Objeto objeto : objetos) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					Object[] array = Util.criarArray(conexao, objeto, null);
					form.getDesktop().addForm(array, new Point(x, y), null, (String) array[Util.ARRAY_INDICE_APE],
							false, config);
					objeto.setSelecionado(false);
					x += 25;
					y += 25;
				}
			}

			formulario.checarPreferenciasLarguraAltura();
			PosicaoDimensao pd = formulario.criarPosicaoDimensaoSeValido();

			if (pd != null) {
				form.setBounds(pd.getX(), pd.getY(), pd.getLargura(), pd.getAltura());
			} else {
				form.setLocationRelativeTo(formulario);
			}

			form.setVisible(true);
		}

		private void destacarDesk(Formulario formulario, List<Objeto> objetos, Conexao conexao, ConfigArquivo config) {
			Desktop desktop = desktops.novo(formulario);

			int x = 10;
			int y = 10;

			for (Objeto objeto : objetos) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					Object[] array = Util.criarArray(conexao, objeto, null);
					desktop.addForm(array, new Point(x, y), null, (String) array[Util.ARRAY_INDICE_APE], false, config);
					objeto.setSelecionado(false);
					x += 25;
					y += 25;
				}
			}

			desktop.ini(getGraphics());
			SwingUtilities.invokeLater(() -> desktop.getDistribuicao().distribuir(-Constantes.VINTE));
		}

		private void destacarProp(Formulario formulario, List<Objeto> objetos, Conexao conexao, Superficie superficie,
				ConfigArquivo config) {
			boolean salvar = false;

			ChaveValor cvDeltaX = VariaveisModelo.get(Constantes.DELTA_X_AJUSTE_FORM_OBJETO);
			ChaveValor cvDeltaY = VariaveisModelo.get(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO);

			if (cvDeltaX == null) {
				cvDeltaX = new ChaveValor(Constantes.DELTA_X_AJUSTE_FORM_OBJETO, Constantes.VAZIO + Constantes.TRINTA);
				VariaveisModelo.adicionar(cvDeltaX);
				salvar = true;
			}

			if (cvDeltaY == null) {
				cvDeltaY = new ChaveValor(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO, Constantes.VAZIO + Constantes.TRINTA);
				VariaveisModelo.adicionar(cvDeltaY);
				salvar = true;
			}

			if (salvar) {
				VariaveisModelo.salvar();
				VariaveisModelo.inicializar();
			}

			for (Objeto objeto : objetos) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					Object[] array = Util.criarArray(conexao, objeto, null);
					superficie.addForm(array,
							new Point(objeto.getX() + cvDeltaX.getInteiro(Constantes.TRINTA),
									objeto.getY() + cvDeltaY.getInteiro(Constantes.TRINTA)),
							null, (String) array[Util.ARRAY_INDICE_APE], false, config);
					objeto.setSelecionado(false);
				}
			}

			superficie.repaint();
		}

		private void destacarObjt(Formulario formulario, List<Objeto> listaObjetos, Conexao conexao) {
			for (Objeto objeto : listaObjetos) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					Superficie.setComplemento(conexao, objeto);
					objetos.novo(formulario, conexao, objeto);
					objeto.setSelecionado(false);
				}
			}
		}
	}

	public SalvarAberto getSalvarAberto() {
		return salvarAberto;
	}

	public Configuracao getConfiguracao() {
		return configuracao;
	}

	public MetadadoTree getMetadadoTree() {
		return metadadoTree;
	}

	public RuntimeExec getRuntimeExec() {
		return runtimeExec;
	}

	public ArquivoTree getArquivoTree() {
		return arquivoTree;
	}

	public Mapeamento getMapeamento() {
		return mapeamento;
	}

	public Comparacao getComparacao() {
		return comparacao;
	}

	public Requisicao getRequisicao() {
		return requisicao;
	}

	public Variaveis getVariaveis() {
		return variaveis;
	}

	public Fragmento getFragmento() {
		return fragmento;
	}

	public Conteiner getConteiner() {
		return conteiner;
	}

	public AnexoTree getAnexoTree() {
		return anexoTree;
	}

	public Destacar getDestacar() {
		return destacar;
	}

	public Anotacao getAnotacao() {
		return anotacao;
	}

	public Conexoes getConexoes() {
		return conexoes;
	}

	public Arquivos getArquivos() {
		return arquivos;
	}

	public Desktops getDesktops() {
		return desktops;
	}

	public Consulta getConsulta() {
		return consulta;
	}

	public Objetos getObjetos() {
		return objetos;
	}

	public Update getUpdate() {
		return update;
	}

	public class Consulta {
		public void nova(Formulario formulario, Conexao conexao) {
			ConsultaContainer container = new ConsultaContainer(null, formulario, formulario, conexao, null, null,
					true);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, ConsultaContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			ConsultaFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, ConsultaContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			ConsultaFormulario.criar(formulario, formulario, container.getConexaoPadrao(), container.getConteudo());
		}

		public void retornoAoFichario(Formulario formulario, ConsultaContainer container) {
			addTab(Constantes.LABEL_CONSULTA, Constantes.LABEL_CONSULTA_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.TABELA);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_CONSULTA));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class Update {
		public void novo(Formulario formulario, Conexao conexao) {
			UpdateContainer container = new UpdateContainer(null, formulario, formulario, conexao, null, null);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, UpdateContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			UpdateFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, UpdateContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			UpdateFormulario.criar(formulario, formulario, container.getConexaoPadrao(), container.getConteudo());
		}

		public void retornoAoFichario(Formulario formulario, UpdateContainer container) {
			addTab(Constantes.LABEL_ATUALIZAR, Constantes.LABEL_ATUALIZAR_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.UPDATE);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class MetadadoTree {
		public void novo(Formulario formulario, Conexao conexao) {
			MetadadoTreeContainer container = new MetadadoTreeContainer(null, formulario, formulario, conexao);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, MetadadoTreeContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			MetadadoTreeFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, MetadadoTreeContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			MetadadoTreeFormulario.criar(formulario, formulario, container.getConexaoPadrao());
		}

		public void retornoAoFichario(Formulario formulario, MetadadoTreeContainer container) {
			addTab(Constantes.LABEL_METADADOS, Constantes.LABEL_METADADOS_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.CAMPOS);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_METADADOS));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class Conexoes {
		public void nova(Formulario formulario) {
			ConexaoContainer container = new ConexaoContainer(null, formulario);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, ConexaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			ConexaoFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, ConexaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			ConexaoFormulario.criar(formulario);
		}

		public void retornoAoFichario(Formulario formulario, ConexaoContainer container) {
			addTab(Constantes.LABEL_CONEXAO, Constantes.LABEL_CONEXAO_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.BANCO);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_CONEXAO));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);

			container.ini(getGraphics());
		}
	}

	public class Requisicao {
		public void nova(Formulario formulario) {
			RequisicaoContainer container = new RequisicaoContainer(null, formulario, null, null);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, RequisicaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			RequisicaoFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, RequisicaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			RequisicaoFormulario.criar(formulario, container.getConteudo(), container.getIdPagina());
		}

		public void retornoAoFichario(Formulario formulario, RequisicaoContainer container) {
			addTab(Constantes.LABEL_REQUISICAO, Constantes.LABEL_REQUISICAO_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.URL);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_REQUISICAO));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class RuntimeExec {
		public void novo(Formulario formulario) {
			RuntimeExecContainer container = new RuntimeExecContainer(null, formulario, null, null);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, RuntimeExecContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			RuntimeExecFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, RuntimeExecContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			RuntimeExecFormulario.criar(formulario, container.getConteudo(), container.getIdPagina());
		}

		public void retornoAoFichario(Formulario formulario, RuntimeExecContainer container) {
			addTab(Constantes.LABEL_RUNTIME_EXEC, Constantes.LABEL_RUNTIME_EXEC_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.EXECUTAR);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_RUNTIME_EXEC));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class Configuracao {
		public void nova(Formulario formulario) {
			ConfiguracaoContainer container = new ConfiguracaoContainer(null, formulario);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, ConfiguracaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			ConfiguracaoFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, ConfiguracaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			ConfiguracaoFormulario.criar(formulario);
		}

		public void retornoAoFichario(Formulario formulario, ConfiguracaoContainer container) {
			addTab(Constantes.LABEL_CONFIGURACOES, Constantes.LABEL_CONFIGURACOES_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.CONFIG);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_CONFIGURACOES));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class Comparacao {
		public void nova(Formulario formulario) {
			ComparacaoContainer container = new ComparacaoContainer(null, formulario);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, ComparacaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			ComparacaoFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, ComparacaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			ComparacaoFormulario.criar(formulario);
		}

		public void retornoAoFichario(Formulario formulario, ComparacaoContainer container) {
			addTab(Constantes.LABEL_COMPARACAO, Constantes.LABEL_COMPARACAO_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.CENTRALIZAR);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_COMPARACAO));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class Mapeamento {
		public void novo(Formulario formulario) {
			MapeamentoContainer container = new MapeamentoContainer(null, formulario);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, MapeamentoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			MapeamentoFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, MapeamentoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			MapeamentoFormulario.criar(formulario);
		}

		public void retornoAoFichario(Formulario formulario, MapeamentoContainer container) {
			addTab(Constantes.LABEL_MAPEAMENTOS, Constantes.LABEL_MAPEAMENTOS_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.REFERENCIA);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
			container.ini(getGraphics());
		}
	}

	public class Variaveis {
		public void novo(Formulario formulario) {
			VariaveisContainer container = new VariaveisContainer(null, formulario);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, VariaveisContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			VariaveisFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, VariaveisContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			VariaveisFormulario.criar(formulario);
		}

		public void retornoAoFichario(Formulario formulario, VariaveisContainer container) {
			addTab(Constantes.LABEL_VARIAVEIS, Constantes.LABEL_VARIAVEIS_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.VAR);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_VARIAVEIS));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
			container.ini(getGraphics());
		}
	}

	public class Fragmento {
		public void novo(Formulario formulario) {
			FragmentoContainer container = new FragmentoContainer(null, formulario, null);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, FragmentoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			FragmentoFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, FragmentoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			FragmentoFormulario.criar(formulario);
		}

		public void retornoAoFichario(Formulario formulario, FragmentoContainer container) {
			addTab(Constantes.LABEL_FRAGMENTO, Constantes.LABEL_FRAGMENTO_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.FRAGMENTO);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
			container.ini(getGraphics());
		}
	}

	public class Conteiner {
		public Container novo(Formulario formulario) {
			Container container = new Container(formulario, null);
			container.getSuperficie().setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
			addTab(Mensagens.getString(Constantes.LABEL_NOVO), container);
			container.setAbortarFecharComESCSuperficie(true);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.CUBO);
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
			container.estadoSelecao();

			return container;
		}

		public void destacarEmFormulario(Formulario formulario, Container container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);

			File file = container.getArquivo();

			if (file == null) {
				file = new File(Constantes.DESTACADO);
			}

			ContainerFormulario.criar(formulario, container, file);
		}

		public void retornoAoFichario(Formulario formulario, Container container) {
			File file = container.getArquivo();

			if (file == null) {
				file = new File(Constantes.DESTACADO);
			}

			addTab(file.getName(), container);
			int ultimoIndice = getTabCount() - 1;

			container.getSuperficie().setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
			container.setAbortarFecharComESCSuperficie(true);

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.CUBO);
			setTabComponentAt(ultimoIndice, tituloAba);
			setToolTipTextAt(ultimoIndice, file.getAbsolutePath());
			setTitleAt(ultimoIndice, file.getName());
			setSelectedIndex(ultimoIndice);
			container.estadoSelecao();
		}

		public void abrirExportacaoMetadado(Formulario formulario, Metadado metadado, boolean circular) {
			Container container = novo(formulario);
			container.abrirExportacaoImportacaoMetadado(metadado, true, circular);
			setTitleAt(getTabCount() - 1, Mensagens.getString("label.abrir_exportacao"));
		}

		public void abrirImportacaoMetadado(Formulario formulario, Metadado metadado, boolean circular) {
			Container container = novo(formulario);
			container.abrirExportacaoImportacaoMetadado(metadado, false, circular);
			setTitleAt(getTabCount() - 1, Mensagens.getString("label.abrir_importacao"));
		}

		public void exportarMetadadoRaiz(Formulario formulario, Metadado metadado) {
			if (metadado.getEhRaiz() && !metadado.estaVazio()) {
				Container container = novo(formulario);
				container.exportarMetadadoRaiz(metadado);
				setTitleAt(getTabCount() - 1, Mensagens.getString("label.exportar"));
			}
		}
	}

	public boolean excluirAba(FicharioAba aba) {
		int indice = arquivos.getIndice(aba.getComponent());

		if (indice == -1) {
			return false;
		}

		remove(indice);
		return true;
	}

	public void adicionarAba(FicharioAba aba) {
		if (aba == null) {
			throw new IllegalArgumentException("Aba nula.");
		}

		addTab(aba.getChaveTitulo(), aba.getChaveTituloMin(), aba.getComponent());
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, aba.getIcone());
		setToolTipTextAt(ultimoIndice, aba.getHintTitulo());
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public void addTab(String title, String titleMin, Component component) {
		addTab(Preferencias.isTituloAbaMin() ? Mensagens.getString(titleMin) : Mensagens.getString(title), component);
	}

	public class Anotacao {
		public void nova(Formulario formulario) {
			AnotacaoContainer container = new AnotacaoContainer(null, formulario, null);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, AnotacaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			AnotacaoFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, AnotacaoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			AnotacaoFormulario.criar(formulario, container.getConteudo());
		}

		public void retornoAoFichario(Formulario formulario, AnotacaoContainer container) {
			addTab(Constantes.LABEL_ANOTACOES, Constantes.LABEL_ANOTACOES_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.PANEL4);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ANOTACOES));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class AnexoTree {
		public void novo(Formulario formulario) {
			AnexoTreeContainer container = new AnexoTreeContainer(null, formulario);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, AnexoTreeContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			AnexoTreeFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, AnexoTreeContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			AnexoTreeFormulario.criar(formulario);
		}

		public void retornoAoFichario(Formulario formulario, AnexoTreeContainer container) {
			addTab(Constantes.LABEL_ANEXOS, Constantes.LABEL_ANEXOS_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.ANEXO);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ANEXOS));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class ArquivoTree {
		public void nova(Formulario formulario) {
			ArquivoTreeContainer container = new ArquivoTreeContainer(null, formulario);
			retornoAoFichario(formulario, container);
		}

		public void destacarEmFormulario(Formulario formulario, ArquivoTreeContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);
			ArquivoTreeFormulario.criar(formulario, container);
		}

		public void clonarEmFormulario(Formulario formulario, ArquivoTreeContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			ArquivoTreeFormulario.criar(formulario);
		}

		public void retornoAoFichario(Formulario formulario, ArquivoTreeContainer container) {
			addTab(Constantes.LABEL_ARQUIVOS, Constantes.LABEL_ARQUIVOS_MIN, container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.EXPANDIR);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ARQUIVOS));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);
		}
	}

	public class Desktops {
		public Desktop novo(Formulario formulario) {
			Desktop desktop = new Desktop(formulario, false);
			desktop.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
			addTab(Constantes.LABEL_DESKTOP, Constantes.LABEL_DESKTOP_MIN, desktop);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.PANEL2);
			setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_DESKTOP));
			setTabComponentAt(ultimoIndice, tituloAba);
			setSelectedIndex(ultimoIndice);

			return desktop;
		}
	}

	public class Objetos {
		public void novo(Formulario formulario, Conexao padrao, Objeto objeto) {
			ObjetoContainer container = new ObjetoContainer(null, formulario, padrao, objeto, getGraphics(), false);
			container.setComponenteListener(Fichario.this::getThis);
			container.setDimensaoListener(Fichario.this::getSize);
			addTab(objeto.getId(), container);
			int ultimoIndice = getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(Fichario.this, Icones.CRIAR);
			setTabComponentAt(ultimoIndice, tituloAba);
			container.setSuporte(Fichario.this);
			setSelectedIndex(ultimoIndice);

			container.ini(getGraphics());
		}
	}

	public class Arquivos {
		public void abrir(Formulario formulario, File file, XMLColetor coletor, ConfigArquivo config) {

			if (file.getName().equalsIgnoreCase(Constantes.FVF_SEPARADOR)) {
				addTab(null, null);
				int ultimoIndice = getTabCount() - 1;
				TituloAbaS tituloAba = new TituloAbaS(Fichario.this, file);
				setTabComponentAt(ultimoIndice, tituloAba);
				setBackgroundAt(ultimoIndice, Color.MAGENTA);
				setEnabledAt(ultimoIndice, false);
				return;
			}

			Container container = conteiner.novo(formulario);
			int ultimoIndice = getTabCount() - 1;
			container.abrir(file, coletor, getGraphics(), config);
			setToolTipTextAt(ultimoIndice, file.getAbsolutePath());
			setTitleAt(ultimoIndice, file.getName());
		}

		public void selecionarAba(File file) {
			int indice = getIndice(file);

			if (indice >= 0) {
				setSelectedIndex(indice);
			}
		}

		public void fecharArquivo(File file) {
			if (file == null || !file.isFile()) {
				return;
			}

			int indice = getIndice(file);

			while (indice >= 0) {
				remove(indice);
				indice = getIndice(file);
			}
		}

		public boolean isAberto(File file) {
			return getIndice(file) >= 0;
		}

		public boolean isAtivo(File file) {
			int pos = getIndice(file);
			int sel = getSelectedIndex();
			return pos != -1 && pos == sel;
		}

		public int getIndice(File file) {
			int total = getTabCount();

			for (int i = 0; i < total; i++) {
				try {
					Component cmp = getComponentAt(i);

					if (cmp instanceof Container) {
						Container c = (Container) cmp;

						if (c.getArquivo() != null && file != null
								&& c.getArquivo().getAbsolutePath().equals(file.getAbsolutePath())) {
							return i;
						}
					}
				} catch (Exception e) {
					LOG.log(Level.SEVERE, Constantes.ERRO, e);
				}
			}

			return -1;
		}

		public int getIndice(Component cmpConteudo) {
			int total = getTabCount();

			if (cmpConteudo != null) {
				for (int i = 0; i < total; i++) {
					Component cmp = getComponentAt(i);

					if (cmpConteudo == cmp) {
						return i;
					}
				}
			}

			return -1;
		}

		public int getIndice(Container c) {
			int total = getTabCount();

			for (int i = 0; i < total; i++) {
				Component cmp = getComponentAt(i);

				if ((cmp instanceof Container) && cmp == c) {
					return i;
				}
			}

			return -1;
		}

		public void fecharTodos() {
			int count = getTabCount();

			while (count > 0) {
				removeTabAt(0);
				count = getTabCount();
			}
		}
	}

	@Override
	public void remove(int index) {
		Component cmp = getComponentAt(index);

		if (cmp instanceof Container) {
			((Container) cmp).excluido();
		}

		super.remove(index);
	}

	public void selecionarConexao(Conexao conexao) {
		int total = getTabCount();

		for (int i = 0; i < total; i++) {
			Component cmp = getComponentAt(i);

			if (cmp instanceof IFicharioConexao) {
				IFicharioConexao aba = (IFicharioConexao) cmp;
				aba.selecionarConexao(conexao);
			}
		}
	}

	public void infoConexao() {
		int total = getTabCount();
		StringBuilder sb = new StringBuilder();
		int cont = 0;

		for (int i = 0; i < total; i++) {
			Component cmp = getComponentAt(i);

			if (cmp instanceof IFicharioConexao) {
				IFicharioConexao aba = (IFicharioConexao) cmp;
				InfoConexao info = aba.getInfoConexao();

				sb.append("ABA: " + info.getNomeAba() + Constantes.QL);
				sb.append("ATUAL: " + info.getConexaoAtual() + Constantes.QL);

				if (!Util.estaVazio(info.getConexaoFile())) {
					sb.append("FILE: " + info.getConexaoFile() + Constantes.QL);
				}

				sb.append(Constantes.QL);

				cont++;
			}
		}

		if (sb.length() > 0) {
			sb.insert(0, "TOTAL = " + cont + Constantes.QL + Constantes.QL);
		}

		Util.mensagem(Fichario.this, sb.toString());
	}

	public static String getAbsRelativoArquivo(File diretorioArquivos, File file) {
		if (diretorioArquivos != null && file != null) {
			String absDiretorioArquivos = diretorioArquivos.getAbsolutePath();
			String absArquivoAba = file.getAbsolutePath();
			String nomeArquivoAba = file.getName();

			int pos = posLocalArquivo(diretorioArquivos, file);

			if (pos != -1) {
				String restante = absArquivoAba.substring(pos + absDiretorioArquivos.length());
				absArquivoAba = Util.replaceAll(restante, Constantes.SEPARADOR, Constantes.SEP);
			} else if (nomeArquivoAba.startsWith(Constantes.III)) {
				absArquivoAba = nomeArquivoAba;
			}

			return absArquivoAba;
		}

		return null;
	}

	private static int posLocalArquivo(File diretorio, File arquivo) {
		if (!diretorio.isDirectory() || !arquivo.isFile()) {
			return -1;
		}

		String absDiretorio = diretorio.getAbsolutePath();
		String absArquivo = arquivo.getAbsolutePath();

		return absArquivo.indexOf(absDiretorio);
	}

	public static File getArquivoNormalizado(File file) {
		if (file != null) {
			String nome = file.getName();

			if (nome.startsWith(Constantes.SEP)) {
				nome = Util.replaceAll(nome, Constantes.SEP, Constantes.SEPARADOR);
				file = new File(ArquivoTreeModelo.FILE.getAbsolutePath() + nome);
			}
		}

		return file;
	}

	public class SalvarAberto {
		public void salvar() {
			try (PrintWriter pw = new PrintWriter(Constantes.ABERTOS_FICHARIO, StandardCharsets.UTF_8.name())) {
				int total = getTabCount();

				for (int i = 0; i < total; i++) {
					Component aba = getTabComponentAt(i);
					Component cmp = getComponentAt(i);

					File file = null;

					if (cmp instanceof IFicharioSalvar) {
						file = ((IFicharioSalvar) cmp).getFileSalvarAberto();
					} else if (aba instanceof IFicharioSalvar) {
						file = ((IFicharioSalvar) aba).getFileSalvarAberto();
					}

					String absRelativoArquivo = getAbsRelativoArquivo(ArquivoTreeModelo.FILE, file);

					if (absRelativoArquivo != null) {
						pw.print(absRelativoArquivo + Constantes.QL);
					}
				}

			} catch (Exception ex) {
				LOG.log(Level.SEVERE, ex.getMessage());
			}
		}

		public void abrir(Formulario formulario) {
			File file = new File(Constantes.ABERTOS_FICHARIO);

			if (file.exists()) {
				List<File> files = new ArrayList<>();

				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
					String linha = br.readLine();

					while (linha != null) {
						files.add(new File(linha));
						linha = br.readLine();
					}
				} catch (Exception ex) {
					Util.stackTraceAndMessage("FICHARIO", ex, Fichario.this);
				}

				for (File f : files) {
					abrirArquivo(f, formulario);
				}
			}
		}

		private void abrirArquivo(File f, Formulario formulario) {
			String nome = f.getName();

			if (nome.startsWith(Constantes.III)) {
				formulario.adicionarFicharioAba(nome);
			} else {
				formulario.getArquivos().abrir(getArquivoNormalizado(f), true, null);
			}
		}
	}

	public static class InfoConexao {
		final String conexaoAtual;
		final String conexaoFile;
		final String nomeAba;

		public InfoConexao(String conexaoAtual, String conexaoFile, String nomeAba) {
			this.conexaoAtual = conexaoAtual;
			this.conexaoFile = conexaoFile;
			this.nomeAba = nomeAba;
		}

		public String getConexaoAtual() {
			return conexaoAtual;
		}

		public String getConexaoFile() {
			return conexaoFile;
		}

		public String getNomeAba() {
			return nomeAba;
		}
	}

	private class Listener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int indice = indexAtLocation(x, y);
			ponto = new Ponto(x, y);
			ultX = x;
			ultY = y;

			if (indice != -1) {
				rectangle = getBoundsAt(indice);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (rectangle != null) {
				int recX = e.getX();
				int recY = e.getY();
				rectangle.x += recX - ultX;
				rectangle.y += recY - ultY;
				ultX = recX;
				ultY = recY;
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			rectangle = null;

			if (ponto == null) {
				repaint();
				return;
			}

			int destino = indexAtLocation(e.getX(), e.getY());
			int origem = indexAtLocation(ponto.x, ponto.y);

			if (origem != -1 && destino != -1 && origem != destino) {
				inverter(origem, destino);
			}

			repaint();
		}

		private void inverter(int origem, int destino) {
			Component aba = getTabComponentAt(origem);
			Component cmp = getComponentAt(origem);
			String hint = getToolTipTextAt(origem);
			String titulo = getTitleAt(origem);
			Icon icon = getIconAt(origem);
			remove(origem);

			insertTab(titulo, icon, cmp, hint, destino);
			setTabComponentAt(destino, aba);

			if (aba instanceof TituloAbaS) {
				setEnabledAt(destino, false);
			} else {
				setSelectedIndex(destino);
			}
		}
	}

	private class Ponto {
		final int x;
		final int y;

		Ponto(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}