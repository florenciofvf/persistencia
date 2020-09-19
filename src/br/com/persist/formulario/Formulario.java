package br.com.persist.formulario;

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
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.marca.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final transient Map<String, FabricaContainer> fabricas = new HashMap<>();
	private final transient List<Servico> servicos = new ArrayList<>();
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private static final Logger LOG = Logger.getGlobal();
	private final Fichario fichario = new Fichario();

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
		fichario.adicionarPagina(pagina);
	}

	public int getIndicePagina(Pagina pagina) {
		return fichario.getIndice(pagina);
	}

	public void selecionarPagina(File file) {
		fichario.selecionarPagina(file);
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
		if (servicos == null) {
			return;
		}

		for (Servico servico : servicos) {
			adicionarServico(servico);
		}
	}

	public void adicionarFabrica(String chave, FabricaContainer fabrica) {
		if (chave != null && !chave.trim().isEmpty() && fabrica != null) {
			fabricas.put(chave, fabrica);
		}
	}

	public FabricaContainer getFabrica(String chave) {
		return fabricas.get(chave);
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

				for (Servico servico : servicos) {
					servico.visivelFormulario(Formulario.this);
				}

				fichario.visivelFormulario(Formulario.this);
				fichario.ativarNavegacao();
				iconeBandeja();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				for (Servico servico : servicos) {
					servico.fechandoFormulario(Formulario.this);
				}
			}
		});
	}

	private class MenuPrincipal extends JMenuBar {
		private static final long serialVersionUID = 1L;

		private void carregarMenu() {
			File file = new File("persistencia.xml");

			if (!file.isFile()) {
				return;
			}

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
		itemFechar.addActionListener(e -> eventoFechar());
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

	private Rectangle criarPosicaoDimensaoSeValido() {
		final int espaco = 3;
		Dimension formularioSize = getSize();
		Point formularioLocation = getLocation();
		Rectangle monitorBounds = getGraphicsConfiguration().getBounds();
		double porcentagemLargura = Util.menorEmPorcentagem(formularioSize.width, monitorBounds.width);
		double porcentagemAltura = Util.menorEmPorcentagem(formularioSize.height, monitorBounds.height);

		if (porcentagemAltura <= Preferencias.getPorcVerticalLocalForm()) {
			int x = formularioLocation.x;
			int y = formularioLocation.y + formularioSize.height + espaco;
			int l = formularioSize.width;
			int a = monitorBounds.height - formularioSize.height - espaco;
			return new Rectangle(x, y, l, a);

		} else if (porcentagemLargura <= Preferencias.getPorcHorizontalLocalForm()) {
			int x = formularioLocation.x + formularioSize.width + espaco;
			int y = formularioLocation.y;
			int l = monitorBounds.width - formularioSize.width - espaco;
			int a = formularioSize.height;
			return new Rectangle(x, y, l, a);
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

	private void checarPreferenciasLarguraAltura() {
		if (Preferencias.isAplicarLarguraAoAbrirArquivoObjeto()) {
			definirLarguraEmPorcentagem(Preferencias.getPorcHorizontalLocalForm());
		}

		if (Preferencias.isAplicarAlturaAoAbrirArquivoObjeto()) {
			definirAlturaEmPorcentagem(Preferencias.getPorcVerticalLocalForm());
		}
	}

	public static void posicionarJanela(Formulario formulario, Window window) {
		if (formulario == null || window == null) {
			return;
		}
		formulario.checarPreferenciasLarguraAltura();
		Rectangle pd = formulario.criarPosicaoDimensaoSeValido();

		if (pd != null) {
			window.setBounds((int) pd.getX(), (int) pd.getY(), (int) pd.getWidth(), (int) pd.getHeight());
		} else {
			window.setLocationRelativeTo(formulario);
		}

		window.setVisible(true);
	}
}