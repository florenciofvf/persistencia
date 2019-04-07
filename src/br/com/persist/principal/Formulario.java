package br.com.persist.principal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.desktop.Superficie;
import br.com.persist.dialogo.AnotacaoDialogo;
import br.com.persist.dialogo.ConexaoDialogo;
import br.com.persist.dialogo.ConfigDialogo;
import br.com.persist.dialogo.ConsultaDialogo;
import br.com.persist.dialogo.FragmentoDialogo;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.AnotacaoFormulario;
import br.com.persist.formulario.ArvoreFormulario;
import br.com.persist.formulario.ConexaoFormulario;
import br.com.persist.formulario.ConfigFormulario;
import br.com.persist.formulario.ConsultaFormulario;
import br.com.persist.formulario.DesktopFormulario;
import br.com.persist.formulario.FragmentoFormulario;
import br.com.persist.modelo.ConexaoModelo;
import br.com.persist.modelo.FragmentoModelo;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.ListaArray;
import br.com.persist.util.Macro;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Formulario extends JFrame implements ConexaoProvedor {
	private static final long serialVersionUID = 1L;
	private final transient List<Conexao> conexoes = new ListaArray<>();
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private static final List<Objeto> COPIADOS = new ArrayList<>();
	private final Fichario fichario = new Fichario();
	public static final Macro macro = new Macro();
	private File arquivo;

	public Formulario() {
		super(Mensagens.getString("label.persistencia"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		FormularioUtil.configMAC(this);
		setLayout(new BorderLayout());
		setJMenuBar(menuPrincipal);
		setSize(1000, 600);
		montarLayout();
		configurar();
	}

	public void destacar(Conexao conexao, Superficie superficie, byte tipoContainer) {
		fichario.destacar(this, conexao, superficie.getSelecionados(), tipoContainer);
	}

	public static void copiar(Superficie superficie) {
		COPIADOS.clear();

		for (Objeto objeto : superficie.getSelecionados()) {
			COPIADOS.add(objeto.clonar());
		}
	}

	public static boolean copiadosIsEmpty() {
		return COPIADOS.isEmpty();
	}

	public static void colar(Superficie superficie, boolean b, int x, int y) {
		superficie.limparSelecao();

		for (Objeto objeto : COPIADOS) {
			Objeto clone = get(objeto, superficie);
			superficie.addObjeto(clone);
			clone.setSelecionado(true);
			clone.controlado = true;

			if (b) {
				clone.x = x;
				clone.y = y;
			}
		}
	}

	private static Objeto get(Objeto objeto, Superficie superficie) {
		Objeto o = objeto.clonar();
		o.x += Objeto.diametro;
		o.y += Objeto.diametro;
		o.setId(objeto.getId() + "-" + Objeto.getSequencia());

		boolean contem = superficie.contem(o);

		while (contem) {
			o.setId(objeto.getId() + "-" + Objeto.novaSequencia());
			contem = superficie.contem(o);
		}

		return o;
	}

	@Override
	public List<Conexao> getConexoes() {
		return conexoes;
	}

	public Fichario getFichario() {
		return fichario;
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
				FormularioUtil.aparenciaPadrao(menuPrincipal.menuLAF, "Nimbus");
				fichario.novaArvore(Formulario.this);
				atualizarFragmentos();
				atualizarConexoes();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				FormularioUtil.fechar(Formulario.this);
			}
		});
	}

	public void atualizarFragmentos() {
		try {
			FragmentoModelo.inicializar();
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ATUALIZAR FRAGMENTOS", ex, this);
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
		private Action fecharAcao = Action.actionMenu("label.fechar", Icones.SAIR);
		private Action novoAcao = Action.actionMenu("label.novo", Icones.CUBO);
		final Menu menuArquivo = new Menu("label.arquivo");
		final Menu menuLAF = new Menu("label.aparencia");

		MenuPrincipal() {
			FormularioUtil.menuAparencia(Formulario.this, menuLAF);
			menuArquivo.add(new MenuItem(novoAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuDesktop());
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuConsulta());
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuAnotacao());
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuAbrir());
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuArvore());
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuConexao());
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuFragmento());
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuConfig());
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(fecharAcao));
			add(menuArquivo);
			add(menuLAF);

			eventos();
		}

		private void eventos() {
			novoAcao.setActionListener(e -> fichario.novo(Formulario.this));
			fecharAcao.setActionListener(e -> {
				FormularioUtil.fechar(Formulario.this);
				System.exit(0);
			});
		}

		class MenuAnotacao extends Menu {
			private static final long serialVersionUID = 1L;
			Action formularioAcao = Action.actionMenuFormulario();
			Action ficharioAcao = Action.actionMenuFichario();
			Action dialogoAcao = Action.actionMenuDialogo();

			MenuAnotacao() {
				super(Constantes.LABEL_ANOTACOES, Icones.PANEL4);
				addMenuItem(formularioAcao);
				addMenuItem(ficharioAcao);
				addMenuItem(dialogoAcao);

				formularioAcao.setActionListener(e -> {
					AnotacaoFormulario form = new AnotacaoFormulario();
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					AnotacaoDialogo form = new AnotacaoDialogo(Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(e -> fichario.novaAnotacao(Formulario.this));
			}
		}

		class MenuConsulta extends Menu {
			private static final long serialVersionUID = 1L;
			Action formularioAcao = Action.actionMenuFormulario();
			Action ficharioAcao = Action.actionMenuFichario();
			Action dialogoAcao = Action.actionMenuDialogo();

			MenuConsulta() {
				super(Constantes.LABEL_CONSULTA, Icones.PANEL3);
				addMenuItem(formularioAcao);
				addMenuItem(ficharioAcao);
				addMenuItem(dialogoAcao);

				formularioAcao.setActionListener(e -> {
					ConsultaFormulario form = new ConsultaFormulario(Formulario.this, null);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					ConsultaDialogo form = new ConsultaDialogo(Formulario.this, Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(e -> fichario.novaConsulta(Formulario.this));
			}
		}

		class MenuDesktop extends Menu {
			private static final long serialVersionUID = 1L;
			Action formularioAcao = Action.actionMenuFormulario();
			Action ficharioAcao = Action.actionMenuFichario();

			MenuDesktop() {
				super(Constantes.LABEL_DESKTOP, Icones.PANEL2);
				addMenuItem(formularioAcao);
				addMenuItem(ficharioAcao);

				formularioAcao.setActionListener(e -> {
					DesktopFormulario form = new DesktopFormulario(Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(e -> fichario.novoDesktop(Formulario.this));
			}
		}

		class MenuArvore extends Menu {
			private static final long serialVersionUID = 1L;
			Action formularioAcao = Action.actionMenuFormulario();
			Action ficharioAcao = Action.actionMenuFichario();

			MenuArvore() {
				super(Constantes.LABEL_ARVORE, Icones.EXPANDIR);
				addMenuItem(formularioAcao);
				addMenuItem(ficharioAcao);

				formularioAcao.setActionListener(e -> {
					ArvoreFormulario form = new ArvoreFormulario(Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(e -> fichario.novaArvore(Formulario.this));
			}
		}

		class MenuAbrir extends Menu {
			private static final long serialVersionUID = 1L;
			Action formularioAcao = Action.actionMenuFormulario();
			Action ficharioAcao = Action.actionMenuFichario();

			MenuAbrir() {
				super("label.abrir", Icones.ABRIR);
				addMenuItem(formularioAcao);
				addMenuItem(ficharioAcao);

				formularioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
				ficharioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));

				eventos();
			}

			void eventos() {
				formularioAcao.setActionListener(e -> {
					JFileChooser fileChooser = Util.criarFileChooser(arquivo, true);
					int opcao = fileChooser.showOpenDialog(Formulario.this);

					if (opcao != JFileChooser.APPROVE_OPTION) {
						return;
					}

					File[] files = fileChooser.getSelectedFiles();

					if (files == null) {
						return;
					}

					for (File file : files) {
						abrirArquivo(file, false);
					}
				});

				ficharioAcao.setActionListener(e -> {
					JFileChooser fileChooser = Util.criarFileChooser(arquivo, true);
					int opcao = fileChooser.showOpenDialog(Formulario.this);

					if (opcao != JFileChooser.APPROVE_OPTION) {
						return;
					}

					File[] files = fileChooser.getSelectedFiles();

					if (files == null) {
						return;
					}

					for (File file : files) {
						abrirArquivo(file, true);
					}
				});
			}
		}

		class MenuConfig extends Menu {
			private static final long serialVersionUID = 1L;
			Action formularioAcao = Action.actionMenuFormulario();
			Action ficharioAcao = Action.actionMenuFichario();
			Action dialogoAcao = Action.actionMenuDialogo();

			MenuConfig() {
				super(Constantes.LABEL_CONFIGURACOES, Icones.CONFIG);
				addMenuItem(formularioAcao);
				addMenuItem(ficharioAcao);
				addMenuItem(dialogoAcao);

				formularioAcao.setActionListener(e -> {
					ConfigFormulario form = new ConfigFormulario(Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					ConfigDialogo form = new ConfigDialogo(Formulario.this, Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(e -> fichario.novoConfig(Formulario.this));
			}
		}

		class MenuConexao extends Menu {
			private static final long serialVersionUID = 1L;
			Action formularioAcao = Action.actionMenuFormulario();
			Action ficharioAcao = Action.actionMenuFichario();
			Action dialogoAcao = Action.actionMenuDialogo();

			MenuConexao() {
				super(Constantes.LABEL_CONEXAO, Icones.BANCO);
				addMenuItem(formularioAcao);
				addMenuItem(ficharioAcao);
				addMenuItem(dialogoAcao);

				formularioAcao.setActionListener(e -> {
					ConexaoFormulario form = new ConexaoFormulario(Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					ConexaoDialogo form = new ConexaoDialogo(Formulario.this, Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(e -> fichario.novaConexao(Formulario.this));
			}
		}

		class MenuFragmento extends Menu {
			private static final long serialVersionUID = 1L;
			Action formularioAcao = Action.actionMenuFormulario();
			Action ficharioAcao = Action.actionMenuFichario();
			Action dialogoAcao = Action.actionMenuDialogo();

			MenuFragmento() {
				super(Constantes.LABEL_FRAGMENTO, Icones.FRAGMENTO);
				addMenuItem(formularioAcao);
				addMenuItem(ficharioAcao);
				addMenuItem(dialogoAcao);

				formularioAcao.setActionListener(e -> {
					FragmentoFormulario form = new FragmentoFormulario(Formulario.this);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					FragmentoDialogo form = new FragmentoDialogo(Formulario.this, null);
					form.setLocationRelativeTo(Formulario.this);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(e -> fichario.novoFragmento(Formulario.this));
			}
		}
	}

	public void abrirArquivo(File file, boolean abrirNoFichario) {
		if (file == null || !file.isFile()) {
			return;
		}

		try {
			arquivo = file.getParentFile();
			StringBuilder sbConexao = new StringBuilder();
			List<Relacao> relacoes = new ArrayList<>();
			List<Objeto> objetos = new ArrayList<>();
			List<Form> forms = new ArrayList<>();
			Dimension d = XML.processar(file, objetos, relacoes, forms, sbConexao);

			if (abrirNoFichario) {
				fichario.abrir(Formulario.this, file, objetos, relacoes, forms, sbConexao, d);
			} else {
				fichario.abrirFormulario(Formulario.this, file, objetos, relacoes, forms, sbConexao, d);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
		}
	}
}

class ItemLAF extends JRadioButtonMenuItem {
	private static final long serialVersionUID = 1L;
	private final String classe;

	public ItemLAF(Formulario formulario, LookAndFeelInfo info) {
		classe = info.getClassName();
		setText(info.getName());

		addActionListener(e -> {
			try {
				UIManager.setLookAndFeel(classe);
				SwingUtilities.updateComponentTreeUI(formulario);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(getClass().getName() + ".ItemMenu()", ex, formulario);
			}
		});
	}
}