package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.comp.Button;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.dialogo.DialogoConexao;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private final Toolbar toolbar = new Toolbar();
	private final Superficie superficie;
	private boolean aberto;

	public Formulario() {
		setTitle(Mensagens.getString("label.persistencia"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		superficie = new Superficie(this);
		FormularioUtil.configMAC(this);
		setLayout(new BorderLayout());
		setJMenuBar(menuPrincipal);
		setSize(800, 600);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, superficie);
		add(BorderLayout.NORTH, toolbar);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if (aberto) {
					return;
				}

				aberto = true;
				exemplo();
				FormularioUtil.aparenciaPadrao(menuPrincipal.menuLAF, "Nimbus");
			}

			public void windowClosing(WindowEvent e) {
				FormularioUtil.fechar(Formulario.this);
			};
		});
	}

	private void exemplo() {
		Objeto[] objetos = {
				new Objeto(300, 200, Color.BLACK/* , Icones.ARVORE */),
				new Objeto(10, 210/* , Icones.ALERTA */), new Objeto(20, 20, Color.BLUE) };

		for (Objeto objeto : objetos) {
			superficie.addObjeto(objeto);
		}

		superficie.addRelacao(new Relacao(objetos[2], true, objetos[0], true));
		superficie.addRelacao(new Relacao(objetos[0], true, objetos[1]));
		superficie.addRelacao(new Relacao(objetos[1], objetos[2]));

		superficie.repaint();
	}

	private class MenuPrincipal extends JMenuBar {
		private static final long serialVersionUID = 1L;
		final Menu menuArquivo = new Menu("label.arquivo");
		final Menu menuLAF = new Menu("label.aparencia");

		MenuPrincipal() {
			FormularioUtil.menuAparencia(Formulario.this, menuLAF);
			menuArquivo.add(new MenuItem(new AbrirAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new ConexaoAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new FecharAcao(true)));
			add(menuArquivo);
			add(menuLAF);
			configurar();
		}
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new FecharAcao(false)));
			addSeparator();
			add(new Button(new ConexaoAcao(false)));
			addSeparator();
			add(new Button(new AbrirAcao(false)));
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
	};

	private class ConexaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ConexaoAcao(boolean menu) {
			super(menu, "label.conexao", Icones.BANCO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new DialogoConexao(Formulario.this);
		}
	};

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
					// abrirArquivo(file, true, true, true);
				}
			}
		}
	};
}