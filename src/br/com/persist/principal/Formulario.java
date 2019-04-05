package br.com.persist.principal;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import br.com.persist.dialogo.ConexaoDialogo;
import br.com.persist.dialogo.ConfigDialogo;
import br.com.persist.dialogo.FragmentoDialogo;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.AnotacaoFormulario;
import br.com.persist.formulario.ArvoreFormulario;
import br.com.persist.formulario.ConsultaFormulario;
import br.com.persist.formulario.DesktopFormulario;
import br.com.persist.modelo.ConexaoModelo;
import br.com.persist.modelo.FragmentoModelo;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.Macro;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Formulario extends JFrame implements ConexaoProvedor {
	private static final long serialVersionUID = 1L;
	private final transient List<Conexao> conexoes = new ArrayList<>();
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

	public void destacar(Conexao conexao, Superficie superficie, boolean formDesktop) {
		fichario.destacar(this, conexao, superficie.getSelecionados(), formDesktop);
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
				menuPrincipal.arvoreFichAcao.actionPerformed(null);
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
		private Action arvoreFormAcao = Action.actionMenu("label.arvore_formulario", Icones.EXPANDIR);
		private Action anotaFormAcao = Action.actionMenu("label.anotacoes_formulario", Icones.PANEL4);
		private Action arvoreFichAcao = Action.actionMenu("label.arvore_fichario", Icones.EXPANDIR);
		private Action anotaFichAcao = Action.actionMenu("label.anotacoes_fichario", Icones.PANEL4);
		private Action consFormAcao = Action.actionMenu("label.consulta_formulario", Icones.PANEL3);
		private Action consFichAcao = Action.actionMenu("label.consulta_fichario", Icones.PANEL3);
		private Action fragmentoAcao = Action.actionMenu("label.fragmento", Icones.FRAGMENTO);
		private Action configAcao = Action.actionMenu("label.configuracoes", Icones.CONFIG);
		private Action conexaoAcao = Action.actionMenu("label.conexao", Icones.BANCO);
		private Action formAcao = Action.actionMenu("label.formulario", Icones.PANEL);
		private Action deskAcao = Action.actionMenu("label.desktop", Icones.PANEL2);
		private Action fecharAcao = Action.actionMenu("label.fechar", Icones.SAIR);
		private Action novoAcao = Action.actionMenu("label.novo", Icones.CUBO);
		final Menu menuArquivo = new Menu("label.arquivo");
		final Menu menuLAF = new Menu("label.aparencia");

		MenuPrincipal() {
			FormularioUtil.menuAparencia(Formulario.this, menuLAF);
			menuArquivo.add(new MenuItem(novoAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(deskAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(formAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(consFormAcao));
			menuArquivo.add(new MenuItem(consFichAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(anotaFormAcao));
			menuArquivo.add(new MenuItem(anotaFichAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new AbrirFormularioAcao(true)));
			menuArquivo.add(new MenuItem(new AbrirFicharioAcao(true)));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(arvoreFormAcao));
			menuArquivo.add(new MenuItem(arvoreFichAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(conexaoAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(fragmentoAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(configAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(fecharAcao));
			add(menuArquivo);
			add(menuLAF);

			eventos();
		}

		private void eventos() {
			fragmentoAcao.setActionListener(e -> new FragmentoDialogo(Formulario.this, null).setVisible(true));
			arvoreFormAcao.setActionListener(e -> new ArvoreFormulario(Formulario.this));
			anotaFichAcao.setActionListener(e -> fichario.novaAnotacao(Formulario.this));
			arvoreFichAcao.setActionListener(e -> fichario.novaArvore(Formulario.this));
			consFichAcao.setActionListener(e -> fichario.novaConsulta(Formulario.this));
			conexaoAcao.setActionListener(e -> new ConexaoDialogo(Formulario.this));
			formAcao.setActionListener(e -> new DesktopFormulario(Formulario.this));
			deskAcao.setActionListener(e -> fichario.novoDesktop(Formulario.this));
			configAcao.setActionListener(e -> new ConfigDialogo(Formulario.this));
			novoAcao.setActionListener(e -> fichario.novo(Formulario.this));
			anotaFormAcao.setActionListener(e -> anotacaoFormulario());
			consFormAcao.setActionListener(e -> consultaFormulario());
			fecharAcao.setActionListener(e -> {
				FormularioUtil.fechar(Formulario.this);
				System.exit(0);
			});
		}

		private void anotacaoFormulario() {
			AnotacaoFormulario form = new AnotacaoFormulario();
			form.setLocationRelativeTo(this);
			form.setVisible(true);
		}

		private void consultaFormulario() {
			ConsultaFormulario form = new ConsultaFormulario(Formulario.this, null);
			form.setLocationRelativeTo(this);
			form.setVisible(true);
		}

		class AbrirFormularioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			AbrirFormularioAcao(boolean menu) {
				super(menu, "label.abrir_formulario", Icones.ABRIR);
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = Util.criarFileChooser(arquivo, true);
				int opcao = fileChooser.showOpenDialog(Formulario.this);

				if (opcao == JFileChooser.APPROVE_OPTION) {
					File[] files = fileChooser.getSelectedFiles();

					if (files != null) {
						for (File file : files) {
							abrirArquivo(file, false);
						}
					}
				}
			}
		}

		class AbrirFicharioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			AbrirFicharioAcao(boolean menu) {
				super(menu, "label.abrir_fichario", Icones.ABRIR);
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = Util.criarFileChooser(arquivo, true);
				int opcao = fileChooser.showOpenDialog(Formulario.this);

				if (opcao == JFileChooser.APPROVE_OPTION) {
					File[] files = fileChooser.getSelectedFiles();

					if (files != null) {
						for (File file : files) {
							abrirArquivo(file, true);
						}
					}
				}
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