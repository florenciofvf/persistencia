package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private final Fichario fichario = new Fichario();

	public Formulario() {
		super(Mensagens.getString("label.persistencia"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		FormularioUtil.configMAC(this);
		setLayout(new BorderLayout());
		setJMenuBar(menuPrincipal);
		setSize(800, 600);
		montarLayout();
		configurar();
	}

	public Fichario getFichario() {
		return fichario;
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, fichario);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if (!System.getProperty("os.name").startsWith("Mac OS")) {
					FormularioUtil.aparenciaPadrao(menuPrincipal.menuLAF, "Nimbus");
				}
			}

			public void windowClosing(WindowEvent e) {
				FormularioUtil.fechar(Formulario.this);
			};
		});
	}

	private class MenuPrincipal extends JMenuBar {
		private static final long serialVersionUID = 1L;
		final Menu menuArquivo = new Menu("label.arquivo");
		final Menu menuLAF = new Menu("label.aparencia");

		MenuPrincipal() {
			FormularioUtil.menuAparencia(Formulario.this, menuLAF);
			menuArquivo.add(new MenuItem(new NovoAcao(true)));
			menuArquivo.add(new MenuItem(new AbrirAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new FecharAcao(true)));
			add(menuArquivo);
			add(menuLAF);
		}
	}

	private class NovoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public NovoAcao(boolean menu) {
			super(menu, "label.novo", Icones.NOVO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Container container = new Container(Formulario.this);
			fichario.addTab(Mensagens.getString("label.novo"), container);
			int ultimoIndice = fichario.getTabCount() - 1;

			TituloAba tituloAba = new TituloAba(fichario);
			fichario.setTabComponentAt(ultimoIndice, tituloAba);
			fichario.setSelectedIndex(ultimoIndice);
		}
	}

	private class AbrirAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public AbrirAcao(boolean menu) {
			super(menu, "label.abrir", Icones.ABRIR);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(".");
			int opcao = fileChooser.showOpenDialog(Formulario.this);

			if (opcao == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();

				if (file != null) {
					try {
						List<Relacao> relacoes = new ArrayList<>();
						List<Objeto> objetos = new ArrayList<>();
						XML.processar(file, objetos, relacoes);

						Container container = new Container(Formulario.this);
						container.abrir(file, objetos, relacoes);
						fichario.addTab(Mensagens.getString("label.novo"), container);
						int ultimoIndice = fichario.getTabCount() - 1;

						TituloAba tituloAba = new TituloAba(fichario);
						fichario.setTabComponentAt(ultimoIndice, tituloAba);
						fichario.setTitleAt(ultimoIndice, file.getName());
						fichario.setSelectedIndex(ultimoIndice);
					} catch (Exception ex) {
						Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
					}
				}
			}
		}
	}

	private class FecharAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FecharAcao(boolean menu) {
			super(menu, "label.fechar", Icones.SAIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			FormularioUtil.fechar(Formulario.this);
			System.exit(0);
		}
	}
}