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
import br.com.persist.fichario.Fichario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import br.com.persist.util.PosicaoDimensao;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final transient Map<String, FabricaContainer> fabricas = new HashMap<>();
	private final transient List<Servico> servicos = new ArrayList<>();
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private static final Map<String, Object> map = new HashMap<>();
	private static final Logger LOG = Logger.getGlobal();
	private final Fichario fichario = new Fichario();

	// private final transient List<Conexao> conexoes = new ArrayList<>();
	// public static final Macro macro = new Macro();

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

	public void processar(String comando, Object objeto) {
		for (Servico servico : servicos) {
			servico.processar(this, comando, objeto);
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

				// FormularioUtil.aparenciaPadrao(menuPrincipal.menuLAF,
				// "Nimbus" + Constantes.DOIS);
				// MapeamentoModelo.inicializar();
				// VariaveisModelo.inicializar();
				// FragmentoModelo.inicializar();
				// atualizarConexoes();

				fichario.restaurarPaginas(Formulario.this);
				fichario.ativarNavegacao();
				iconeBandeja();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				fichario.salvarPaginas(Formulario.this);

				for (Servico servico : servicos) {
					servico.fechandoFormulario(Formulario.this);
				}
			}
		});
	}

	// public void destacar(Conexao conexao, Superficie superficie, int
	// tipoContainer, ConfigArquivo config) {
	// fichario.getDestacar().destacar(this, conexao, superficie, tipoContainer,
	// config);
	// }

	// public static class CopiarColar {
	// private static final List<Objeto> copiados = new ArrayList<>();
	//
	// private CopiarColar() {
	// }
	//
	// public static void copiar(Superficie superficie) {
	// copiados.clear();
	//
	// for (Objeto objeto : superficie.getSelecionados()) {
	// copiados.add(objeto.clonar());
	// }
	// }
	//
	// public static void colar(Superficie superficie, boolean b, int x, int y)
	// {
	// superficie.limparSelecao();
	//
	// for (Objeto objeto : copiados) {
	// Objeto clone = get(objeto, superficie);
	// superficie.addObjeto(clone);
	// clone.setSelecionado(true);
	// clone.setControlado(true);
	//
	// if (b) {
	// clone.setX(x);
	// clone.setY(y);
	// }
	// }
	//
	// superficie.repaint();
	// }
	//
	// public static boolean copiadosIsEmpty() {
	// return copiados.isEmpty();
	// }
	//
	// private static Objeto get(Objeto objeto, Superficie superficie) {
	// Objeto o = objeto.clonar();
	// o.deltaX(Objeto.DIAMETRO);
	// o.deltaY(Objeto.DIAMETRO);
	// o.setId(objeto.getId() + "-" + Objeto.getSequencia());
	//
	// boolean contem = superficie.contem(o);
	//
	// while (contem) {
	// o.setId(objeto.getId() + "-" + Objeto.novaSequencia());
	// contem = superficie.contem(o);
	// }
	//
	// return o;
	// }
	// }

	// @Override
	// public List<Conexao> getConexoes() {
	// return conexoes;
	// }

	/*
	 * public class Conteiner { public void abrirExportacaoMetadado(Metadado
	 * metadado, boolean circular) { ObjetoFormulario form =
	 * ObjetoFormulario.criar(Formulario.this, new
	 * File(Mensagens.getString("label.abrir_exportacao")));
	 * form.abrirExportacaoImportacaoMetadado(metadado, true, circular);
	 * form.setLocationRelativeTo(Formulario.this); form.setVisible(true); }
	 * 
	 * public void abrirImportacaoMetadado(Metadado metadado, boolean circular)
	 * { ObjetoFormulario form = ObjetoFormulario.criar(Formulario.this, new
	 * File(Mensagens.getString("label.abrir_importacao")));
	 * form.abrirExportacaoImportacaoMetadado(metadado, false, circular);
	 * form.setLocationRelativeTo(Formulario.this); form.setVisible(true); }
	 * 
	 * public void exportarMetadadoRaiz(Metadado metadado) { if
	 * (metadado.getEhRaiz() && !metadado.estaVazio()) { ObjetoFormulario form =
	 * ObjetoFormulario.criar(Formulario.this, new
	 * File(Mensagens.getString("label.exportar")));
	 * form.exportarMetadadoRaiz(metadado);
	 * form.setLocationRelativeTo(Formulario.this); form.setVisible(true); } } }
	 */

	/*
	 * public class Arquivos { File arquivoParent;
	 * 
	 * public void abrir(File file, boolean abrirNoFichario, ConfigArquivo
	 * config) { if (file == null || !file.isFile()) { return; }
	 * 
	 * try { ObjetoColetor coletor = new ObjetoColetor(); arquivoParent =
	 * file.getParentFile(); XML.processar(file, coletor);
	 * 
	 * if (abrirNoFichario) { fichario.getArquivos().abrir(Formulario.this,
	 * file, coletor, config); } else { abrir(Formulario.this, file, coletor,
	 * config); } } catch (Exception ex) { Util.stackTraceAndMessage("ABRIR: " +
	 * file.getAbsolutePath(), ex, Formulario.this); } }
	 * 
	 * public void abrir(Formulario formulario, File file, ObjetoColetor
	 * coletor, ConfigArquivo config) { ObjetoFormulario form =
	 * ObjetoFormulario.criar(formulario, file); form.abrir(file, coletor,
	 * getGraphics(), config);
	 * 
	 * formulario.checarPreferenciasLarguraAltura(); PosicaoDimensao pd =
	 * formulario.criarPosicaoDimensaoSeValido();
	 * 
	 * if (pd != null) { form.setBounds(pd.getX(), pd.getY(), pd.getLargura(),
	 * pd.getAltura()); } else { form.setLocationRelativeTo(formulario); }
	 * 
	 * form.setVisible(true); } }
	 */

	// public void atualizarConexoes() {
	// ConexaoModelo modelo = new ConexaoModelo();
	// conexoes.clear();
	//
	// try {
	// modelo.abrir();
	// for (Conexao conexao : modelo.getConexoes()) {
	// conexoes.add(conexao);
	// }
	// } catch (Exception ex) {
	// Util.stackTraceAndMessage("ATUALIZAR CONEXOES", ex, this);
	// }
	// }

	private class MenuPrincipal extends JMenuBar {
		private static final long serialVersionUID = 1L;

		private void carregarMenu() {
			File file = new File("menu.xml");

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