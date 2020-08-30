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

import br.com.persist.arquivo.ArquivoTreeModelo;
import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.conexao.Conexao;
import br.com.persist.icone.Icones;
import br.com.persist.metadado.Metadado;
import br.com.persist.objeto.Objeto;
import br.com.persist.objeto.ObjetoContainer;
import br.com.persist.objeto.ObjetoFormulario;
import br.com.persist.objeto.Superficie;
import br.com.persist.objeto.Desktop;
import br.com.persist.objeto.DesktopFormulario;
import br.com.persist.objeto.OTabelaContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import br.com.persist.util.PosicaoDimensao;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.variaveis.VariaveisModelo;
import br.com.persist.xml.XMLColetor;

public class Fichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private final transient NavegButton navegButtonEsquerdo = new NavegButton(1);
	private final transient NavegButton navegButtonDireito = new NavegButton(2);
	private final transient NavegButton navegButtonLimpar = new NavegButton(0);
	private final transient SalvarAbrir salvarAbrir = new SalvarAbrir();
	private final transient Conteiner conteiner = new Conteiner();
	private final transient NavegacaoListener navegacaoListener;
	private final transient Arquivos arquivos = new Arquivos();
	private final transient Destacar destacar = new Destacar();
	private final transient Listener listener = new Listener();
	private final transient Desktops desktops = new Desktops();
	private final transient Objetos objetos = new Objetos();
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

	public Conteiner getConteiner() {
		return conteiner;
	}

	public Destacar getDestacar() {
		return destacar;
	}

	public Arquivos getArquivos() {
		return arquivos;
	}

	public Desktops getDesktops() {
		return desktops;
	}

	public Objetos getObjetos() {
		return objetos;
	}

	public class Conteiner {
		public ObjetoContainer novo(Formulario formulario) {
			ObjetoContainer container = new ObjetoContainer(formulario, null);
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

		public void destacarEmFormulario(Formulario formulario, ObjetoContainer container) {
			int indice = arquivos.getIndice(container);

			if (indice == -1) {
				return;
			}

			remove(indice);

			File file = container.getArquivo();

			if (file == null) {
				file = new File(Constantes.DESTACADO);
			}

			ObjetoFormulario.criar(formulario, container, file);
		}

		public void retornoAoFichario(Formulario formulario, ObjetoContainer container) {
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
			ObjetoContainer container = novo(formulario);
			container.abrirExportacaoImportacaoMetadado(metadado, true, circular);
			setTitleAt(getTabCount() - 1, Mensagens.getString("label.abrir_exportacao"));
		}

		public void abrirImportacaoMetadado(Formulario formulario, Metadado metadado, boolean circular) {
			ObjetoContainer container = novo(formulario);
			container.abrirExportacaoImportacaoMetadado(metadado, false, circular);
			setTitleAt(getTabCount() - 1, Mensagens.getString("label.abrir_importacao"));
		}

		public void exportarMetadadoRaiz(Formulario formulario, Metadado metadado) {
			if (metadado.getEhRaiz() && !metadado.estaVazio()) {
				ObjetoContainer container = novo(formulario);
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
			OTabelaContainer container = new OTabelaContainer(null, formulario, padrao, objeto, getGraphics(), false);
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

			ObjetoContainer container = conteiner.novo(formulario);
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

					if (cmp instanceof ObjetoContainer) {
						ObjetoContainer c = (ObjetoContainer) cmp;

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

		public int getIndice(ObjetoContainer c) {
			int total = getTabCount();

			for (int i = 0; i < total; i++) {
				Component cmp = getComponentAt(i);

				if ((cmp instanceof ObjetoContainer) && cmp == c) {
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

		if (cmp instanceof ObjetoContainer) {
			((ObjetoContainer) cmp).excluido();
		}

		super.remove(index);
	}

	public void selecionarConexao(Conexao conexao) {
		int total = getTabCount();

		for (int i = 0; i < total; i++) {
			Component cmp = getComponentAt(i);

			if (cmp instanceof FicharioConexao) {
				FicharioConexao aba = (FicharioConexao) cmp;
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

			if (cmp instanceof FicharioConexao) {
				FicharioConexao aba = (FicharioConexao) cmp;
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

			int pos = posicaoArquivo(diretorioArquivos, file);

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

	private static int posicaoArquivo(File diretorio, File arquivo) {
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

	public void salvarAbertos() {
		salvarAbrir.salvar();
	}

	public void abrirSalvos(Formulario formulario) {
		salvarAbrir.abrir(formulario);
	}

	public class SalvarAbrir {
		public void salvar() {
			try (PrintWriter pw = new PrintWriter(Constantes.ABERTOS_FICHARIO, StandardCharsets.UTF_8.name())) {
				int total = getTabCount();

				for (int i = 0; i < total; i++) {
					Component aba = getTabComponentAt(i);
					Component cmp = getComponentAt(i);

					File file = null;

					if (cmp instanceof FicharioSalvar) {
						file = ((FicharioSalvar) cmp).getFileSalvarAberto();
					} else if (aba instanceof FicharioSalvar) {
						file = ((FicharioSalvar) aba).getFileSalvarAberto();
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