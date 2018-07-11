package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.comp.Button;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.dialogo.DialogoConexao;
import br.com.persist.dialogo.RelacaoDialogo;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private final Toolbar toolbar = new Toolbar();
	private final Superficie superficie;
	private File arquivo;

	public Formulario() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		superficie = new Superficie(this);
		FormularioUtil.configMAC(this);
		setLayout(new BorderLayout());
		setJMenuBar(menuPrincipal);
		setSize(800, 600);
		montarLayout();
		configurar();
		titulo();
	}

	private void titulo() {
		if (arquivo == null) {
			setTitle(Mensagens.getString("label.persistencia"));
		} else {
			setTitle(Mensagens.getString("label.persistencia") + " - " + arquivo.getAbsolutePath());
		}
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, superficie);
		add(BorderLayout.NORTH, toolbar);
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
			menuArquivo.add(new MenuItem(new SalvarAcao(true)));
			menuArquivo.add(new MenuItem(new SalvarComoAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new ConexaoAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new FecharAcao(true)));
			add(menuArquivo);
			add(menuLAF);
		}
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new FecharAcao(false)));
			addSeparator();
			add(new Button(new ConexaoAcao(false)));
			addSeparator();
			add(new Button(new NovoAcao(false)));
			add(new Button(new AbrirAcao(false)));
			addSeparator();
			add(new Button(new SalvarAcao(false)));
			add(new Button(new SalvarComoAcao(false)));
			addSeparator();
			add(new Button(new ExcluirObjetoAcao()));
			add(new Button(new ExcluirRelacaoAcao()));
			add(new Button(new CriarObjetoAcao()));
			add(new Button(new CriarRelacaoAcao()));
			addSeparator();
			add(new JToggleButton(new DesenhoIdAcao()));
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

	private class ConexaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ConexaoAcao(boolean menu) {
			super(menu, "label.conexao", Icones.BANCO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new DialogoConexao(Formulario.this);
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
					superficie.abrir(file);
					arquivo = file;
					titulo();
				}
			}
		}
	}

	private class SalvarComoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SalvarComoAcao(boolean menu) {
			super(menu, "label.salvar_como", Icones.SALVARC);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(".");
			int opcao = fileChooser.showSaveDialog(Formulario.this);

			if (opcao == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();

				if (file != null) {
					superficie.salvar(file);
					arquivo = file;
					titulo();
				}
			}
		}
	}

	private class SalvarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SalvarAcao(boolean menu) {
			super(menu, "label.salvar", Icones.SALVAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (arquivo != null) {
				superficie.salvar(arquivo);
			} else {
				new SalvarComoAcao(false).actionPerformed(null);
			}
		}
	}

	private class ExcluirObjetoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirObjetoAcao() {
			super(false, "label.excluir_objeto", Icones.EXCLUIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Objeto objeto = superficie.getSelecionado();
			superficie.excluir(objeto);
			superficie.repaint();
		}
	}

	private class ExcluirRelacaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirRelacaoAcao() {
			super(false, "label.excluir_relacao", Icones.EXCLUIR2);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Objeto objeto2 = superficie.getSelecionado2();
			Objeto objeto1 = superficie.getSelecionado();
			Relacao relacao = superficie.getRelacao(objeto1, objeto2);
			superficie.excluir(relacao);
			superficie.repaint();
		}
	}

	private class CriarObjetoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CriarObjetoAcao() {
			super(false, "label.criar_objeto", Icones.CRIAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			superficie.addObjeto(new Objeto(40, 40));
			superficie.repaint();
		}
	}

	private class CriarRelacaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CriarRelacaoAcao() {
			super(false, "label.criar_relacao", Icones.CRIAR2);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Objeto objeto2 = superficie.getSelecionado2();
			Objeto objeto1 = superficie.getSelecionado();

			if (objeto1 == null || objeto2 == null) {
				return;
			}

			new RelacaoDialogo(Formulario.this, superficie, objeto1, objeto2);
		}
	}

	private class NovoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public NovoAcao(boolean menu) {
			super(menu, "label.novo", Icones.NOVO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			arquivo = null;
			superficie.limpar();
			superficie.repaint();
			titulo();
		}
	}

	private class DesenhoIdAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public DesenhoIdAcao() {
			super(false, "label.desenhar_id", Icones.LABEL);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JToggleButton button = (JToggleButton) e.getSource();
			superficie.desenharIds(button.isSelected());
		}
	}
}