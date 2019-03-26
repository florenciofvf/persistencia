package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.dialogo.ConexaoDialogo;
import br.com.persist.dialogo.ConfigDialogo;
import br.com.persist.dialogo.FragmentoDialogo;
import br.com.persist.modelo.ConexaoModelo;
import br.com.persist.modelo.FragmentoModelo;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.Macro;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	public static final List<Objeto> COPIADOS = new ArrayList<>();
	private final Vector<Conexao> conexoes = new Vector<>();
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
		o.setId(objeto.getId() + "-" + Objeto.getID());

		boolean contem = superficie.contem(o);

		while (contem) {
			o.setId(objeto.getId() + "-" + Objeto.novoID());
			contem = superficie.contem(o);
		}

		return o;
	}

	public Vector<Conexao> getConexoes() {
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
				Preferencias.fichario_com_rolagem ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		fichario.setTabPlacement(Preferencias.posicao_aba_fichario);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				FormularioUtil.aparenciaPadrao(menuPrincipal.menuLAF, "Nimbus");
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
		private Action fragmentoAcao = Action.actionMenu("label.fragmento", Icones.FRAGMENTO);
		private Action configAcao = Action.actionMenu("label.configuracoes", Icones.CONFIG);
		private Action conexaoAcao = Action.actionMenu("label.conexao", Icones.BANCO);
		private Action formAcao = Action.actionMenu("label.formulario", Icones.PANEL);
		private Action consAcao = Action.actionMenu("label.consulta", Icones.PANEL3);
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
			menuArquivo.add(new MenuItem(consAcao));
			menuArquivo.addSeparator();
			menuArquivo.add(new MenuItem(new AbrirFormularioAcao(true)));
			menuArquivo.add(new MenuItem(new AbrirFicharioAcao(true)));
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
			conexaoAcao.setActionListener(e -> new ConexaoDialogo(Formulario.this));
			formAcao.setActionListener(e -> new FormularioDesktop(Formulario.this));
			deskAcao.setActionListener(e -> fichario.novoDesktop(Formulario.this));
			consAcao.setActionListener(e -> fichario.novoSelect(Formulario.this));
			configAcao.setActionListener(e -> new ConfigDialogo(Formulario.this));
			novoAcao.setActionListener(e -> fichario.novo(Formulario.this));
			fecharAcao.setActionListener(e -> {
				FormularioUtil.fechar(Formulario.this);
				System.exit(0);
			});
		}

		class AbrirFormularioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			AbrirFormularioAcao(boolean menu) {
				super(menu, "label.abrir_formulario", Icones.ABRIR);
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setPreferredSize(Constantes.DIMENSION_FILE_CHOOSER);
				fileChooser.setMultiSelectionEnabled(true);

				if (arquivo != null) {
					fileChooser.setCurrentDirectory(arquivo);
				}

				int opcao = fileChooser.showOpenDialog(Formulario.this);

				if (opcao == JFileChooser.APPROVE_OPTION) {
					File[] files = fileChooser.getSelectedFiles();

					if (files != null) {
						for (File file : files) {
							try {
								arquivo = file.getParentFile();
								StringBuilder sbConexao = new StringBuilder();
								List<Relacao> relacoes = new ArrayList<>();
								List<Objeto> objetos = new ArrayList<>();
								List<Form> forms = new ArrayList<>();
								Dimension d = XML.processar(file, objetos, relacoes, forms, sbConexao);
								fichario.abrirFormulario(Formulario.this, file, objetos, relacoes, forms, sbConexao, d);
							} catch (Exception ex) {
								Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
							}
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
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setPreferredSize(Constantes.DIMENSION_FILE_CHOOSER);
				fileChooser.setMultiSelectionEnabled(true);

				if (arquivo != null) {
					fileChooser.setCurrentDirectory(arquivo);
				}

				int opcao = fileChooser.showOpenDialog(Formulario.this);

				if (opcao == JFileChooser.APPROVE_OPTION) {
					File[] files = fileChooser.getSelectedFiles();

					if (files != null) {
						for (File file : files) {
							try {
								arquivo = file.getParentFile();
								StringBuilder sbConexao = new StringBuilder();
								List<Relacao> relacoes = new ArrayList<>();
								List<Objeto> objetos = new ArrayList<>();
								List<Form> forms = new ArrayList<>();
								Dimension d = XML.processar(file, objetos, relacoes, forms, sbConexao);
								fichario.abrir(Formulario.this, file, objetos, relacoes, forms, sbConexao, d);
							} catch (Exception ex) {
								Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
							}
						}
					}
				}
			}
		}
	}
}