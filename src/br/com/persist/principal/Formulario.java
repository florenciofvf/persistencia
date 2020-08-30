package br.com.persist.principal;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.ambiente.AmbienteContainer;
import br.com.persist.ambiente.AmbienteDialogo;
import br.com.persist.ambiente.AmbienteFormulario;
import br.com.persist.anexo.AnexoTreeContainer;
import br.com.persist.anexo.AnexoTreeFormulario;
import br.com.persist.anotacao.AnotacaoDialogo;
import br.com.persist.anotacao.AnotacaoFormulario;
import br.com.persist.arquivo.ArquivoTreeContainer;
import br.com.persist.arquivo.ArquivoTreeFormulario;
import br.com.persist.comparacao.ComparacaoDialogo;
import br.com.persist.comparacao.ComparacaoFormulario;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.SplitPane;
import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoDialogo;
import br.com.persist.conexao.ConexaoFormulario;
import br.com.persist.conexao.ConexaoModelo;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.configuracao.ConfiguracaoDialogo;
import br.com.persist.configuracao.ConfiguracaoFormulario;
import br.com.persist.consulta.ConsultaDialogo;
import br.com.persist.consulta.ConsultaFormulario;
import br.com.persist.container.ContainerFormulario;
import br.com.persist.desktop.DesktopFormulario;
import br.com.persist.fabrica.Fabrica;
import br.com.persist.fabrica.FabricaContainer;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.fragmento.FragmentoDialogo;
import br.com.persist.fragmento.FragmentoFormulario;
import br.com.persist.fragmento.FragmentoModelo;
import br.com.persist.icone.Icones;
import br.com.persist.macro.Macro;
import br.com.persist.mapeamento.MapeamentoDialogo;
import br.com.persist.mapeamento.MapeamentoFormulario;
import br.com.persist.mapeamento.MapeamentoModelo;
import br.com.persist.metadado.Metadado;
import br.com.persist.metadado.MetadadoTreeFormulario;
import br.com.persist.objeto.Objeto;
import br.com.persist.requisicao.RequisicaoDialogo;
import br.com.persist.requisicao.RequisicaoFormulario;
import br.com.persist.runtime_exec.RuntimeExecDialogo;
import br.com.persist.runtime_exec.RuntimeExecFormulario;
import br.com.persist.superficie.Superficie;
import br.com.persist.update.UpdateDialogo;
import br.com.persist.update.UpdateFormulario;
import br.com.persist.util.Action;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import br.com.persist.util.MenuApp;
import br.com.persist.util.MenuPadrao1;
import br.com.persist.util.PosicaoDimensao;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.variaveis.VariaveisDialogo;
import br.com.persist.variaveis.VariaveisFormulario;
import br.com.persist.variaveis.VariaveisModelo;
import br.com.persist.xml.XML;
import br.com.persist.xml.XMLColetor;

public class Formulario extends JFrame implements ConexaoProvedor {
	private static final long serialVersionUID = 1L;
	private final transient List<Conexao> conexoes = new ArrayList<>();
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private static final Map<String, Object> map = new HashMap<>();
	private final transient Conteiner conteiner = new Conteiner();
	private final transient Arquivos arquivos = new Arquivos();
	private static final Logger LOG = Logger.getGlobal();
	private final Fichario fichario = new Fichario();
	public static final Macro macro = new Macro();

	public Formulario() {
		super(Mensagens.getTituloAplicacao());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		setJMenuBar(menuPrincipal);
		setSize(Constantes.SIZE);
		Util.configWindowC(this);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, fichario);
	}

	private void configurar() {
		fichario.setTabLayoutPolicy(
				Preferencias.isFicharioComRolagem() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		fichario.setTabPlacement(Preferencias.getPosicaoAbaFichario());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				FormularioUtil.aparenciaPadrao(menuPrincipal.menuLAF, "Nimbus" + Constantes.DOIS);
				MapeamentoModelo.inicializar();
				VariaveisModelo.inicializar();
				FragmentoModelo.inicializar();
				menuPrincipal.carregarMenu();
				atualizarConexoes();

				fichario.abrirSalvos(Formulario.this);
				fichario.ativarNavegacao();
				iconeBandeja();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// menuPrincipal.fecharAcao.actionPerformed(null);
			}
		});
	}

	public void destacar(Conexao conexao, Superficie superficie, int tipoContainer, ConfigArquivo config) {
		fichario.getDestacar().destacar(this, conexao, superficie, tipoContainer, config);
	}

	public static class CopiarColar {
		private static final List<Objeto> copiados = new ArrayList<>();

		private CopiarColar() {
		}

		public static void copiar(Superficie superficie) {
			copiados.clear();

			for (Objeto objeto : superficie.getSelecionados()) {
				copiados.add(objeto.clonar());
			}
		}

		public static void colar(Superficie superficie, boolean b, int x, int y) {
			superficie.limparSelecao();

			for (Objeto objeto : copiados) {
				Objeto clone = get(objeto, superficie);
				superficie.addObjeto(clone);
				clone.setSelecionado(true);
				clone.setControlado(true);

				if (b) {
					clone.setX(x);
					clone.setY(y);
				}
			}

			superficie.repaint();
		}

		public static boolean copiadosIsEmpty() {
			return copiados.isEmpty();
		}

		private static Objeto get(Objeto objeto, Superficie superficie) {
			Objeto o = objeto.clonar();
			o.deltaX(Objeto.DIAMETRO);
			o.deltaY(Objeto.DIAMETRO);
			o.setId(objeto.getId() + "-" + Objeto.getSequencia());

			boolean contem = superficie.contem(o);

			while (contem) {
				o.setId(objeto.getId() + "-" + Objeto.novaSequencia());
				contem = superficie.contem(o);
			}

			return o;
		}
	}

	@Override
	public List<Conexao> getConexoes() {
		return conexoes;
	}

	public Conteiner getConteiner() {
		return conteiner;
	}

	public Arquivos getArquivos() {
		return arquivos;
	}

	public Fichario getFichario() {
		return fichario;
	}

	public void adicionarFicharioAba(FicharioAba ficharioAba) {
		fichario.adicionarAba(ficharioAba);
	}

	public boolean excluirFicharioAba(FicharioAba ficharioAba) {
		return fichario.excluirAba(ficharioAba);
	}

	public void adicionarFicharioAba(String classeFabricaEContainerDetalhe) {
		if (classeFabricaEContainerDetalhe.startsWith(Constantes.III)) {
			classeFabricaEContainerDetalhe = classeFabricaEContainerDetalhe.substring(Constantes.III.length(),
					classeFabricaEContainerDetalhe.length());
		}

		FabricaContainer fabricaContainer = Fabrica.criar(classeFabricaEContainerDetalhe);

		if (fabricaContainer == null) {
			return;
		}

		FicharioAba ficharioAba = fabricaContainer.criarFicharioAba(this, classeFabricaEContainerDetalhe);
		adicionarFicharioAba(ficharioAba);
	}

	public class Conteiner {
		public void abrirExportacaoMetadado(Metadado metadado, boolean circular) {
			ContainerFormulario form = ContainerFormulario.criar(Formulario.this,
					new File(Mensagens.getString("label.abrir_exportacao")));
			form.abrirExportacaoImportacaoMetadado(metadado, true, circular);
			form.setLocationRelativeTo(Formulario.this);
			form.setVisible(true);
		}

		public void abrirImportacaoMetadado(Metadado metadado, boolean circular) {
			ContainerFormulario form = ContainerFormulario.criar(Formulario.this,
					new File(Mensagens.getString("label.abrir_importacao")));
			form.abrirExportacaoImportacaoMetadado(metadado, false, circular);
			form.setLocationRelativeTo(Formulario.this);
			form.setVisible(true);
		}

		public void exportarMetadadoRaiz(Metadado metadado) {
			if (metadado.getEhRaiz() && !metadado.estaVazio()) {
				ContainerFormulario form = ContainerFormulario.criar(Formulario.this,
						new File(Mensagens.getString("label.exportar")));
				form.exportarMetadadoRaiz(metadado);
				form.setLocationRelativeTo(Formulario.this);
				form.setVisible(true);
			}
		}
	}

	public class Arquivos {
		File arquivoParent;

		public void abrir(File file, boolean abrirNoFichario, ConfigArquivo config) {
			if (file == null || !file.isFile()) {
				return;
			}

			try {
				XMLColetor coletor = new XMLColetor();
				arquivoParent = file.getParentFile();
				XML.processar(file, coletor);

				if (abrirNoFichario) {
					fichario.getArquivos().abrir(Formulario.this, file, coletor, config);
				} else {
					abrir(Formulario.this, file, coletor, config);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
			}
		}

		public void abrir(Formulario formulario, File file, XMLColetor coletor, ConfigArquivo config) {
			ContainerFormulario form = ContainerFormulario.criar(formulario, file);
			form.abrir(file, coletor, getGraphics(), config);

			formulario.checarPreferenciasLarguraAltura();
			PosicaoDimensao pd = formulario.criarPosicaoDimensaoSeValido();

			if (pd != null) {
				form.setBounds(pd.getX(), pd.getY(), pd.getLargura(), pd.getAltura());
			} else {
				form.setLocationRelativeTo(formulario);
			}

			form.setVisible(true);
		}
	}

	public void atualizarConexoes() {
		ConexaoModelo modelo = new ConexaoModelo();
		conexoes.clear();

		try {
			modelo.abrir();
			for (Conexao conexao : modelo.getConexoes()) {
				conexoes.add(conexao);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ATUALIZAR CONEXOES", ex, this);
		}
	}

	private class MenuPrincipal extends JMenuBar {
		private static final long serialVersionUID = 1L;

		private void carregarMenu() {
			File file = new File("menu.xml");

			if (!file.isFile()) {
				return;
			}

			try {
				XMLColetor coletor = new XMLColetor();
				XML.processarMenu(file, coletor);

				for (MenuApp m : coletor.getMenus()) {
					add(m.criarMenu());
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("CARREGAR MENU: " + file.getAbsolutePath(), ex, Formulario.this);
			}
		}

		/*
		 * private class MenuAmbiente extends MenuPadrao1 { private static final
		 * long serialVersionUID = 1L; private final String
		 * classeFabricaEContainerDetalhe;
		 * 
		 * private MenuAmbiente(AmbienteContainer.Ambiente ambiente) {
		 * super(ambiente.getChaveRotulo(), null);
		 * //classeFabricaEContainerDetalhe =
		 * AmbienteContainer.gerarStringArquivo(ambiente);
		 * 
		 * formularioAcao .setActionListener(e ->
		 * AmbienteFormulario.criar(Formulario.this, Constantes.VAZIO,
		 * ambiente)); ficharioAcao.setActionListener(e ->
		 * adicionarFicharioAba(classeFabricaEContainerDetalhe));
		 * dialogoAcao.setActionListener(e ->
		 * AmbienteDialogo.criar(Formulario.this, ambiente)); } }
		 */
	}

	private class MenuDesktop extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuDesktop() {
			super(Constantes.LABEL_DESKTOP, Icones.PANEL2, false);

			ficharioAcao.setActionListener(e -> fichario.getDesktops().novo(Formulario.this));
			formularioAcao.setActionListener(e -> DesktopFormulario.criar(Formulario.this));
		}
	}

	private class MenuAbrir extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAbrir() {
			super("label.abrir", Icones.ABRIR, false);

			formularioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
			ficharioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));

			eventos();
		}

		private void eventos() {
			formularioAcao.setActionListener(e -> {
				File[] files = getSelectedFiles(arquivos.arquivoParent, true);

				if (files == null || files.length == 0) {
					return;
				}

				for (File file : files) {
					arquivos.abrir(file, false, null);
				}
			});

			ficharioAcao.setActionListener(e -> {
				File[] files = getSelectedFiles(arquivos.arquivoParent, true);

				if (files == null || files.length == 0) {
					return;
				}

				for (File file : files) {
					arquivos.abrir(file, true, null);
				}
			});
		}

		private File[] getSelectedFiles(File arquivo, boolean multiSelection) {
			JFileChooser fileChooser = Util.criarFileChooser(arquivo, multiSelection);
			int opcao = fileChooser.showOpenDialog(Formulario.this);

			if (opcao != JFileChooser.APPROVE_OPTION) {
				return new File[0];
			}

			return fileChooser.getSelectedFiles();
		}
	}

	private void fecharFormulario(boolean fecharConexao) {
		if (Util.confirmar(Formulario.this, "label.confirma_fechar")) {
			Preferencias.setFecharConexao(fecharConexao);
			FormularioUtil.fechar(Formulario.this);
			System.exit(0);
		}
	}

	private void iconeBandeja() {
		PopupMenu popup = new PopupMenu();

		java.awt.MenuItem itemFechar = new java.awt.MenuItem(Mensagens.getString(Constantes.LABEL_FECHAR));
		itemFechar.addActionListener(e -> fecharFormulario(false));
		popup.add(itemFechar);

		URL url = getClass().getResource(Constantes.IMAGEM_TRAY_ICON);
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		SystemTray systemTray = SystemTray.getSystemTray();

		TrayIcon trayIcon = new TrayIcon(image, Mensagens.getTituloAplicacao(), popup);
		trayIcon.setImageAutoSize(true);

		try {
			systemTray.add(trayIcon);
			trayIcon.displayMessage(Mensagens.getTituloAplicacao(), Mensagens.getString("versao"),
					TrayIcon.MessageType.INFO);
			trayIcon.addActionListener(new IconeBandejaListener());
		} catch (AWTException ex) {
			LOG.log(Level.SEVERE, Constantes.ERRO, ex);
		}
	}

	private class IconeBandejaListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			LOG.log(Level.FINEST, "IconeBandejaListener.actionPerformed");
		}
	}

	public static Map<String, Object> getMap() {
		return map;
	}

	public PosicaoDimensao criarPosicaoDimensaoSeValido() {
		final int espaco = 3;
		Dimension principalSize = getSize();
		Point principalLocation = getLocation();
		Rectangle configuraSize = getGraphicsConfiguration().getBounds();
		double porcentagemLargura = Util.menorEmPorcentagem(principalSize.width, configuraSize.width);
		double porcentagemAltura = Util.menorEmPorcentagem(principalSize.height, configuraSize.height);

		if (porcentagemAltura <= Preferencias.getPorcVerticalLocalForm()) {
			int x = principalLocation.x;
			int y = principalLocation.y + principalSize.height + espaco;
			int l = principalSize.width;
			int a = configuraSize.height - principalSize.height - espaco;
			return new PosicaoDimensao(x, y, l, a);

		} else if (porcentagemLargura <= Preferencias.getPorcHorizontalLocalForm()) {
			int x = principalLocation.x + principalSize.width + espaco;
			int y = principalLocation.y;
			int l = configuraSize.width - principalSize.width - espaco;
			int a = principalSize.height;
			return new PosicaoDimensao(x, y, l, a);
		}

		return null;
	}

	public void definirAlturaEmPorcentagem(int porcentagem) {
		Dimension principalSize = getSize();
		Rectangle configuraSize = getGraphicsConfiguration().getBounds();
		double altura = Util.porcentagemEmValor(porcentagem, configuraSize.height);
		setSize(principalSize.width, (int) altura);
	}

	public void definirLarguraEmPorcentagem(int porcentagem) {
		Dimension principalSize = getSize();
		Rectangle configuraSize = getGraphicsConfiguration().getBounds();
		double largura = Util.porcentagemEmValor(porcentagem, configuraSize.width);
		setSize((int) largura, principalSize.height);
	}

	public void checarPreferenciasLarguraAltura() {
		if (Preferencias.isAplicarLarguraAoAbrirArquivoObjeto()) {
			definirLarguraEmPorcentagem(Preferencias.getPorcHorizontalLocalForm());
		}

		if (Preferencias.isAplicarAlturaAoAbrirArquivoObjeto()) {
			definirAlturaEmPorcentagem(Preferencias.getPorcVerticalLocalForm());
		}
	}
}