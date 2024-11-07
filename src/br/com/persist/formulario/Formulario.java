package br.com.persist.formulario;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.FabricaContainer;
import br.com.persist.abstrato.Servico;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.marca.XML;
import br.com.persist.plugins.ouvinte.OuvinteFormulario;

public class Formulario extends JFrame {
	private final transient Map<String, FabricaContainer> fabricas = new HashMap<>();
	private final transient List<Servico> servicos = new ArrayList<>();
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private static final Logger LOG = Logger.getGlobal();
	private final Fichario fichario = new Fichario();
	private static final long serialVersionUID = 1L;
	private OuvinteFormulario ouvinteFormulario;

	public Formulario(GraphicsConfiguration gc) {
		super(Mensagens.getTituloAplicacao(), gc);
		ini();
	}

	public Formulario() {
		super(Mensagens.getTituloAplicacao());
		ini();
	}

	public void atualizarTitulo() {
		setTitle(Mensagens.getTituloAplicacao());
	}

	private void ini() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		setJMenuBar(menuPrincipal);
		setSize(Constantes.SIZE);
		Util.configWindowC(this);
		montarLayout();
		configurar();
	}

	public void setHintTitlePagina(int indice, String hint, String title) {
		fichario.setToolTipTextAt(indice, hint);
		fichario.setTitleAt(indice, title);
	}

	public void setTabLayoutPolicy(int tabLayoutPolicy) {
		fichario.setTabLayoutPolicy(tabLayoutPolicy);
	}

	public void setTabPlacement(int tabPlacement) {
		fichario.setTabPlacement(tabPlacement);
	}

	public boolean liberarPagina(Pagina pagina) {
		return fichario.liberarPagina(pagina);
	}

	public boolean excluirPagina(Pagina pagina) {
		return fichario.excluirPagina(pagina);
	}

	public void adicionarPagina(Pagina pagina) {
		try {
			fichario.adicionarPagina(pagina);
		} catch (ArgumentoException ex) {
			Util.mensagem(this, ex.getMessage());
		}
	}

	public int getIndicePagina(Pagina pagina) {
		return fichario.getIndice(pagina);
	}

	public void selecionarPagina(File file) {
		fichario.selecionarPagina(file);
	}

	public void selecionarPagina(int indice) {
		fichario.selecionarPagina(indice);
	}

	public void fecharArquivo(File file) {
		fichario.fecharArquivo(file);
	}

	public boolean isAberto(File file) {
		return fichario.isAberto(file);
	}

	public boolean isAtivo(File file) {
		return fichario.isAtivo(file);
	}

	public void fecharTodos() {
		fichario.fecharTodos();
	}

	public void adicionarServico(Servico servico) {
		if (servico != null) {
			servicos.add(servico);
		}
	}

	public void adicionarServicos(List<Servico> servicos) {
		if (servicos != null) {
			for (Servico servico : servicos) {
				adicionarServico(servico);
			}
		}
	}

	public void adicionarFabrica(String chave, FabricaContainer fabrica) {
		if (!Util.isEmpty(chave) && fabrica != null) {
			fabricas.put(chave, fabrica);
		}
	}

	public FabricaContainer getFabrica(String chave) {
		return fabricas.get(chave);
	}

	public List<FabricaContainer> getFabricas() {
		return new ArrayList<>(fabricas.values());
	}

	public void processar(Map<String, Object> args) {
		fichario.processar(this, args);
		for (Servico servico : servicos) {
			servico.processar(this, args);
		}
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
				menuPrincipal.carregarMenu();
				Preferencias.abrir();
				for (Servico servico : servicos) {
					servico.windowOpenedHandler(Formulario.this);
				}
				fichario.windowOpenedHandler(Formulario.this);
				fichario.ativarNavegacao();
				iconeBandeja();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				for (Servico servico : servicos) {
					servico.windowClosingHandler(Formulario.this);
				}
			}

			@Override
			public void windowActivated(WindowEvent e) {
				fichario.windowActivatedHandler(Formulario.this);
			}
		});
	}

	private class MenuPrincipal extends JMenuBar {
		private static final long serialVersionUID = 1L;

		private void carregarMenu() {
			File file = new File("persistencia.xml");
			if (file.isFile()) {
				carregarMenu(file);
			}
		}

		private void carregarMenu(File file) {
			try {
				MenuColetor coletor = new MenuColetor();
				XML.processar(file, new MenuHandler(coletor));
				for (MenuApp m : coletor.getMenus()) {
					add(m.criarMenu(Formulario.this));
				}
				Collections.sort(servicos, (o1, o2) -> o1.getOrdem() - o2.getOrdem());
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("CARREGAR MENU: " + file.getAbsolutePath(), ex, Formulario.this);
			}
		}
	}

	private void iconeBandeja() {
		PopupMenu popup = new PopupMenu();
		java.awt.MenuItem itemFechar = new java.awt.MenuItem(Mensagens.getString(Constantes.LABEL_FECHAR));
		java.awt.MenuItem itemVersao = new java.awt.MenuItem(Mensagens.getString("versao"));
		popup.add(itemVersao);
		itemFechar.addActionListener(e -> eventoFechar());
		popup.add(itemFechar);
		URL url = getClass().getResource(Constantes.IMAGEM_TRAY_ICON);
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		SystemTray systemTray = SystemTray.getSystemTray();
		TrayIcon trayIcon = criarTryIcon(popup, image);
		iconeBandeja(systemTray, trayIcon);
	}

	private TrayIcon criarTryIcon(PopupMenu popup, Image image) {
		TrayIcon trayIcon = new TrayIcon(image, Mensagens.getTituloAplicacao(), popup);
		trayIcon.setImageAutoSize(true);
		return trayIcon;
	}

	private void iconeBandeja(SystemTray systemTray, TrayIcon trayIcon) {
		try {
			systemTray.add(trayIcon);
			trayIcon.displayMessage(Mensagens.getTituloAplicacao(), Mensagens.getString("versao"),
					TrayIcon.MessageType.INFO);
			trayIcon.addActionListener(new IconeBandejaListener());
		} catch (AWTException ex) {
			LOG.log(Level.SEVERE, Constantes.ERRO, ex);
		}
	}

	public void eventoFechar() {
		WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
	}

	private class IconeBandejaListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			LOG.log(Level.FINEST, "IconeBandejaListener.actionPerformed");
		}
	}

	private Rectangle criarDimensaoOuNull() {
		final int espaco = 3;
		Rectangle form = getBounds();
		Rectangle mont = getGraphicsConfiguration().getBounds();
		if (Preferencias.isAbrirFormularioDireita()) {
			return new Rectangle(form.x + form.width + espaco, form.y,
					mont.width - form.width - Math.abs(form.x - mont.x) - espaco, form.height);
		} else if (Preferencias.isAbrirFormularioAbaixo()) {
			return new Rectangle(form.x, form.y + form.height + espaco, form.width,
					mont.height - form.height - Math.abs(form.y - mont.y) - espaco);
		}
		return null;
	}

	public void checarPreferenciasLarguraAltura() {
		if (Preferencias.isAplicarLarguraAoAbrirArquivoObjeto()) {
			definirLarguraEmPorcentagem(Preferencias.getPorcHorizontalLocalForm());
		}
		if (Preferencias.isAplicarAlturaAoAbrirArquivoObjeto()) {
			definirAlturaEmPorcentagem(Preferencias.getPorcVerticalLocalForm());
		}
	}

	public void definirLarguraEmPorcentagem(int porcentagem) {
		Dimension principalSize = getSize();
		Rectangle configuraSize = getGraphicsConfiguration().getBounds();
		double largura = Util.porcentagemEmValor(porcentagem, configuraSize.width);
		setSize((int) largura, principalSize.height);
	}

	public void definirAlturaEmPorcentagem(int porcentagem) {
		Dimension principalSize = getSize();
		Rectangle configuraSize = getGraphicsConfiguration().getBounds();
		double altura = Util.porcentagemEmValor(porcentagem, configuraSize.height);
		setSize(principalSize.width, (int) altura);
	}

	public static void posicionarJanela(Formulario formulario, Window window) {
		if (formulario != null && window != null) {
			formulario.checarPreferenciasLarguraAltura();
			posicionar(formulario, window);
		}
	}

	private static void posicionar(Formulario formulario, Window window) {
		Rectangle rect = formulario.criarDimensaoOuNull();
		rect = checarDimensoes(rect, formulario.getGraphicsConfiguration().getBounds());
		if (rect != null) {
			window.setBounds((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
		} else {
			window.setLocationRelativeTo(formulario);
		}
		window.setVisible(true);
	}

	private static Rectangle checarDimensoes(Rectangle rect, Rectangle mont) {
		if (mont != null && rect != null) {
			int largura = (int) Util.menorEmPorcentagem(rect.width, mont.width);
			int altura = (int) Util.menorEmPorcentagem(rect.height, mont.height);
			if (largura < 25 || altura < 25) {
				return null;
			}
		}
		return rect;
	}

	public void salvarGC() {
		GraphicsDevice device = getGraphicsConfiguration().getDevice();
		if (device != null) {
			Preferencias.setString(Constantes.GC, device.getIDstring());
		}
	}

	public void salvarMonitorComoPreferencial() {
		GraphicsDevice device = getGraphicsConfiguration().getDevice();
		if (device != null) {
			Preferencias.setString(Constantes.GC_PREFERENCIAL, device.getIDstring());
		}
	}

	public void excluirMonitorComoPreferencial() {
		GraphicsDevice device = getGraphicsConfiguration().getDevice();
		if (device != null) {
			Preferencias.setString(Constantes.GC_PREFERENCIAL, Constantes.VAZIO);
		}
	}

	public void listarNomeBiblio(List<String> lista) {
		lista.addAll(FormularioFabrica.listarNomeBiblio());
	}

	public OuvinteFormulario getOuvinteFormulario() {
		return ouvinteFormulario;
	}

	public void setOuvinteFormulario(OuvinteFormulario ouvinteFormulario) {
		this.ouvinteFormulario = ouvinteFormulario;
	}
}