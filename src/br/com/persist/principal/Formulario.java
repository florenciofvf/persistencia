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

import br.com.persist.Metadado;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.SplitPane;
import br.com.persist.container.AmbienteContainer;
import br.com.persist.container.AnexoContainer;
import br.com.persist.container.ArvoreContainer;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Relacao;
import br.com.persist.desktop.Superficie;
import br.com.persist.dialogo.AmbienteDialogo;
import br.com.persist.dialogo.AnotacaoDialogo;
import br.com.persist.dialogo.ComparacaoDialogo;
import br.com.persist.dialogo.ConexaoDialogo;
import br.com.persist.dialogo.ConfigDialogo;
import br.com.persist.dialogo.ConsultaDialogo;
import br.com.persist.dialogo.FragmentoDialogo;
import br.com.persist.dialogo.MapeamentoDialogo;
import br.com.persist.dialogo.RequisicaoDialogo;
import br.com.persist.dialogo.UpdateDialogo;
import br.com.persist.dialogo.VariaveisDialogo;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.AmbienteFormulario;
import br.com.persist.formulario.AnexoFormulario;
import br.com.persist.formulario.AnotacaoFormulario;
import br.com.persist.formulario.ArvoreFormulario;
import br.com.persist.formulario.ComparacaoFormulario;
import br.com.persist.formulario.ConexaoFormulario;
import br.com.persist.formulario.ConfigFormulario;
import br.com.persist.formulario.ConsultaFormulario;
import br.com.persist.formulario.ContainerFormulario;
import br.com.persist.formulario.DesktopFormulario;
import br.com.persist.formulario.FragmentoFormulario;
import br.com.persist.formulario.MapeamentoFormulario;
import br.com.persist.formulario.MetadadoFormulario;
import br.com.persist.formulario.RequisicaoFormulario;
import br.com.persist.formulario.UpdateFormulario;
import br.com.persist.formulario.VariaveisFormulario;
import br.com.persist.modelo.ConexaoModelo;
import br.com.persist.modelo.FragmentoModelo;
import br.com.persist.modelo.MapeamentoModelo;
import br.com.persist.modelo.VariaveisModelo;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.ListaArray;
import br.com.persist.util.Macro;
import br.com.persist.util.Mensagens;
import br.com.persist.util.MenuPadrao1;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class Formulario extends JFrame implements ConexaoProvedor {
	private static final long serialVersionUID = 1L;
	private final transient List<Conexao> conexoes = new ListaArray<>();
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private final transient Conteiner conteiner = new Conteiner();
	private final transient Arquivos arquivos = new Arquivos();
	private SplitPane splitPane = Util.criarSplitPane(0);
	private final Fichario fichario = new Fichario();
	public static final Macro macro = new Macro();

	public Formulario() {
		super(Mensagens.getString("label.persistencia"));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		setJMenuBar(menuPrincipal);
		setSize(Constantes.SIZE);
		Util.configWindowC(this);
		montarLayout();
		configurar();
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
				atualizarConexoes();

				if (Constantes.ABRIR_AUTO_FICHARIO_SET) {
					menuPrincipal.abrirAutoFichario();
				}

				menuPrincipal.menuLayout.aplicarLayout();
				fichario.getSalvarAberto().abrir(Formulario.this);
				fichario.ativarNavegacao();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				menuPrincipal.fecharAcao.actionPerformed(null);
			}
		});
	}

	public void destacar(Conexao conexao, Superficie superficie, int tipoContainer) {
		fichario.getDestacar().destacar(this, conexao, superficie, tipoContainer);
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

	private void montarLayout() {
		add(BorderLayout.CENTER, fichario);
	}

	public class Conteiner {
		public void abrirExportacaoMetadado(Metadado metadado, boolean circular) {
			ContainerFormulario form = new ContainerFormulario(Formulario.this,
					new File(Mensagens.getString("label.abrir_exportacao")));
			form.abrirExportacaoImportacaoMetadado(metadado, true, circular);
			form.setLocationRelativeTo(Formulario.this);
			form.setVisible(true);
		}

		public void abrirImportacaoMetadado(Metadado metadado, boolean circular) {
			ContainerFormulario form = new ContainerFormulario(Formulario.this,
					new File(Mensagens.getString("label.abrir_importacao")));
			form.abrirExportacaoImportacaoMetadado(metadado, false, circular);
			form.setLocationRelativeTo(Formulario.this);
			form.setVisible(true);
		}
	}

	public class Arquivos {
		File arquivoParent;

		public void abrir(File file, boolean abrirNoFichario) {
			if (file == null || !file.isFile()) {
				return;
			}

			try {
				StringBuilder sbConexao = new StringBuilder();
				List<Relacao> relacoes = new ArrayList<>();
				List<Objeto> objetos = new ArrayList<>();
				arquivoParent = file.getParentFile();
				List<Form> forms = new ArrayList<>();
				Dimension d = XML.processar(file, objetos, relacoes, forms, sbConexao);

				if (abrirNoFichario) {
					fichario.getArquivos().abrir(Formulario.this, file, objetos, relacoes, forms, sbConexao, d);
				} else {
					abrir(Formulario.this, file, objetos, relacoes, forms, sbConexao, d);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
			}
		}

		public void abrir(Formulario formulario, File file, List<Objeto> objetos, List<Relacao> relacoes,
				List<Form> forms, StringBuilder sbConexao, Dimension d) {
			ContainerFormulario form = new ContainerFormulario(formulario, file);
			form.abrir(file, objetos, relacoes, forms, sbConexao, getGraphics(), d);
			form.setLocationRelativeTo(formulario);
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
		private Action novoAcao = Action.actionMenu(Constantes.LABEL_NOVO, Icones.CUBO);
		private final Menu menuConfig = new Menu(Constantes.LABEL_CONFIGURACOES);
		private final Menu menuAmbiente = new Menu(Constantes.LABEL_AMBIENTES);
		private final Menu menuUtil = new Menu(Constantes.LABEL_UTILITARIOS);
		private final Menu menuArquivo = new Menu(Constantes.LABEL_ARQUIVO);
		private final MenuMapeamento itemMapeamento = new MenuMapeamento();
		private final MenuComparacao itemComparacao = new MenuComparacao();
		private final MenuRequisicao itemRequisicao = new MenuRequisicao();
		private final Menu menuLAF = new Menu(Constantes.LABEL_APARENCIA);
		private final Menu menuBanco = new Menu(Constantes.LABEL_BANCO);
		private final MenuFragmento itemFragmento = new MenuFragmento();
		private final MenuVariaveis itemVariavel = new MenuVariaveis();
		private final MenuAnotacao itemAnotacao = new MenuAnotacao();
		private final MenuMetadado itemMetadado = new MenuMetadado();
		private final MenuConsulta itemConsulta = new MenuConsulta();
		private final Action fecharAcao = Action.actionMenuFechar();
		private final MenuDesktop itemDesktop = new MenuDesktop();
		private final MenuArquivo itemArquivo = new MenuArquivo();
		private final MenuConexao itemConexao = new MenuConexao();
		private final MenuUpdate itemUpdate = new MenuUpdate();
		private final MenuConfig itemConfig = new MenuConfig();
		private final MenuLayout menuLayout = new MenuLayout();
		private final MenuAnexo itemAnexo = new MenuAnexo();

		MenuPrincipal() {
			FormularioUtil.menuAparencia(Formulario.this, menuLAF);

			menuArquivo.add(new MenuItem(novoAcao));
			menuArquivo.add(true, itemDesktop);
			menuArquivo.add(true, new MenuAbrir());
			menuArquivo.add(true, itemAnexo);
			menuArquivo.add(true, itemArquivo);
			menuArquivo.add(true, new MenuItem(fecharAcao));
			add(menuArquivo);

			menuBanco.add(itemConexao);
			menuBanco.add(true, itemConsulta);
			menuBanco.add(true, itemUpdate);
			menuBanco.add(true, itemMetadado);
			add(menuBanco);

			menuUtil.add(itemAnotacao);
			menuUtil.add(true, itemFragmento);
			menuUtil.add(true, itemMapeamento);
			menuUtil.add(true, itemVariavel);
			menuUtil.add(true, itemComparacao);
			menuUtil.add(true, itemRequisicao);
			add(menuUtil);

			menuConfig.add(menuLayout);
			menuConfig.add(true, itemConfig);
			add(menuConfig);

			for (MenuAmbiente item : listaMenuAmbiente()) {
				menuAmbiente.add(item);
			}
			add(menuAmbiente);

			add(menuLAF);

			eventos();
		}

		void abrirAutoFichario() {
			itemAnexo.abrirAutoFichario();
			itemArquivo.abrirAutoFichario();
			itemConexao.abrirAutoFichario();
			itemMetadado.abrirAutoFichario();
			itemConsulta.abrirAutoFichario();
			itemUpdate.abrirAutoFichario();
			itemAnotacao.abrirAutoFichario();
			itemFragmento.abrirAutoFichario();
			itemMapeamento.abrirAutoFichario();
			itemVariavel.abrirAutoFichario();
			itemComparacao.abrirAutoFichario();
			itemRequisicao.abrirAutoFichario();
			itemConfig.abrirAutoFichario();
		}

		private void eventos() {
			novoAcao.setActionListener(e -> fichario.getConteiner().novo(Formulario.this));

			fecharAcao.setActionListener(e -> {
				if (Util.confirmar(Formulario.this, "label.confirma_fechar")) {
					FormularioUtil.fechar(Formulario.this);
					System.exit(0);
				}
			});
		}

		private List<MenuAmbiente> listaMenuAmbiente() {
			List<MenuAmbiente> lista = new ArrayList<>();

			for (AmbienteContainer.Ambiente ambiente : AmbienteContainer.Ambiente.values()) {
				lista.add(new MenuAmbiente(ambiente));
			}

			return lista;
		}

		class MenuAmbiente extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAmbiente(AmbienteContainer.Ambiente ambiente) {
				super(ambiente.getChaveLabel(), null);

				formularioAcao
						.setActionListener(e -> AmbienteFormulario.criar(Formulario.this, Constantes.VAZIO, ambiente));
				ficharioAcao.setActionListener(e -> fichario.getAmbientes().novo(Formulario.this, ambiente));
				dialogoAcao.setActionListener(e -> AmbienteDialogo.criar(Formulario.this, ambiente));
			}
		}

		class MenuAnotacao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAnotacao() {
				super(Constantes.LABEL_ANOTACOES, Icones.PANEL4);

				formularioAcao.setActionListener(e -> AnotacaoFormulario.criar(Formulario.this, Constantes.VAZIO));
				ficharioAcao.setActionListener(e -> fichario.getAnotacao().nova(Formulario.this));
				dialogoAcao.setActionListener(e -> AnotacaoDialogo.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_ANOTACAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuRequisicao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuRequisicao() {
				super(Constantes.LABEL_REQUISICAO, Icones.URL);

				formularioAcao.setActionListener(e -> RequisicaoFormulario.criar(Formulario.this, Constantes.VAZIO));
				ficharioAcao.setActionListener(e -> fichario.getRequisicao().nova(Formulario.this));
				dialogoAcao.setActionListener(e -> RequisicaoDialogo.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_REQUISICAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuConsulta extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuConsulta() {
				super(Constantes.LABEL_CONSULTA, Icones.TABELA);

				formularioAcao
						.setActionListener(e -> ConsultaFormulario.criar(Formulario.this, Formulario.this, null, null));
				dialogoAcao.setActionListener(e -> ConsultaDialogo.criar(Formulario.this, Formulario.this, null));
				ficharioAcao.setActionListener(e -> fichario.getConsulta().nova(Formulario.this, null));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_CONSULTA)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuUpdate extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuUpdate() {
				super(Constantes.LABEL_ATUALIZAR, Icones.UPDATE);

				formularioAcao
						.setActionListener(e -> UpdateFormulario.criar(Formulario.this, Formulario.this, null, null));
				dialogoAcao.setActionListener(e -> UpdateDialogo.criar(Formulario.this, Formulario.this, null));
				ficharioAcao.setActionListener(e -> fichario.getUpdate().novo(Formulario.this, null));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_ATUALIZA)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuDesktop extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuDesktop() {
				super(Constantes.LABEL_DESKTOP, Icones.PANEL2, false);

				ficharioAcao.setActionListener(e -> fichario.getDesktops().novo(Formulario.this));
				formularioAcao.setActionListener(e -> DesktopFormulario.criar(Formulario.this));
			}
		}

		class MenuArquivo extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuArquivo() {
				super(Constantes.LABEL_ARQUIVOS, Icones.EXPANDIR, false);

				ficharioAcao.setActionListener(e -> fichario.getArvore().nova(Formulario.this));
				formularioAcao.setActionListener(e -> ArvoreFormulario.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_ARQUIVO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuMetadado extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuMetadado() {
				super(Constantes.LABEL_METADADOS, Icones.CAMPOS, false);

				formularioAcao.setActionListener(e -> MetadadoFormulario.criar(Formulario.this, Formulario.this, null));
				ficharioAcao.setActionListener(e -> fichario.getMetadados().novo(Formulario.this, null));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_METADADO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuAnexo extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAnexo() {
				super(Constantes.LABEL_ANEXOS, Icones.ANEXO, false);

				ficharioAcao.setActionListener(e -> fichario.getAnexos().novo(Formulario.this));
				formularioAcao.setActionListener(e -> AnexoFormulario.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_ANEXO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuAbrir extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrir() {
				super("label.abrir", Icones.ABRIR, false);

				formularioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
				ficharioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));

				eventos();
			}

			void eventos() {
				formularioAcao.setActionListener(e -> {
					File[] files = getSelectedFiles(arquivos.arquivoParent, true);

					if (files == null || files.length == 0) {
						return;
					}

					for (File file : files) {
						arquivos.abrir(file, false);
					}
				});

				ficharioAcao.setActionListener(e -> {
					File[] files = getSelectedFiles(arquivos.arquivoParent, true);

					if (files == null || files.length == 0) {
						return;
					}

					for (File file : files) {
						arquivos.abrir(file, true);
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

		class MenuLayout extends Menu {
			private static final long serialVersionUID = 1L;
			private Action layout1Acao = Action.actionMenu("label.layout_1", null);
			private Action layout2Acao = Action.actionMenu("label.layout_2", null);
			private Action layout3Acao = Action.actionMenu("label.layout_3", null);
			private Action layout4Acao = Action.actionMenu("label.layout_4", null);
			private Action layout5Acao = Action.actionMenu("label.layout_5", null);

			MenuLayout() {
				super("label.layout", Icones.REGION);
				addMenuItem(layout1Acao);
				addMenuItem(layout2Acao);
				addMenuItem(layout3Acao);
				addMenuItem(layout4Acao);
				addMenuItem(layout5Acao);

				layout1Acao.setActionListener(e -> layout1());
				layout2Acao.setActionListener(e -> layout2());
				layout3Acao.setActionListener(e -> layout3());
				layout4Acao.setActionListener(e -> layout4());
				layout5Acao.setActionListener(e -> layout5());
			}

			private void layout1() {
				Formulario.this.remove(splitPane);
				Formulario.this.remove(fichario);

				Formulario.this.add(BorderLayout.CENTER, fichario);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void layout2() {
				Dimension d = Formulario.this.getSize();
				Formulario.this.remove(splitPane);
				Formulario.this.remove(fichario);

				ArvoreContainer arvore = new ArvoreContainer(null, Formulario.this);
				AnexoContainer anexo = new AnexoContainer(null, Formulario.this);
				SplitPane esquerdo = Util.splitPaneVertical(arvore, anexo, d.height / 2);
				splitPane = Util.splitPaneHorizontal(esquerdo, fichario, d.width / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPane);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void layout3() {
				Dimension d = Formulario.this.getSize();
				Formulario.this.remove(splitPane);
				Formulario.this.remove(fichario);

				ArvoreContainer arvore = new ArvoreContainer(null, Formulario.this);
				AnexoContainer anexo = new AnexoContainer(null, Formulario.this);
				SplitPane esquerdo = Util.splitPaneVertical(anexo, arvore, d.height / 2);
				splitPane = Util.splitPaneHorizontal(esquerdo, fichario, d.width / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPane);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void layout4() {
				Dimension d = Formulario.this.getSize();
				Formulario.this.remove(splitPane);
				Formulario.this.remove(fichario);

				ArvoreContainer arvore = new ArvoreContainer(null, Formulario.this);
				AnexoContainer anexo = new AnexoContainer(null, Formulario.this);
				SplitPane abaixo = Util.splitPaneHorizontal(arvore, anexo, d.width / 2);
				splitPane = Util.splitPaneVertical(fichario, abaixo, d.height / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPane);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void layout5() {
				Dimension d = Formulario.this.getSize();
				Formulario.this.remove(splitPane);
				Formulario.this.remove(fichario);

				ArvoreContainer arvore = new ArvoreContainer(null, Formulario.this);
				AnexoContainer anexo = new AnexoContainer(null, Formulario.this);
				SplitPane abaixo = Util.splitPaneHorizontal(anexo, arvore, d.width / 2);
				splitPane = Util.splitPaneVertical(fichario, abaixo, d.height / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPane);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			void aplicarLayout() {
				int valor = Preferencias.getLayoutAbertura();

				if (valor == 1) {
					layout1();
				} else if (valor == 2) {
					layout2();
				} else if (valor == 3) {
					layout3();
				} else if (valor == 4) {
					layout4();
				} else if (valor == 5) {
					layout5();
				}
			}
		}

		class MenuConfig extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuConfig() {
				super(Constantes.LABEL_CONFIGURACOES, Icones.CONFIG);

				ficharioAcao.setActionListener(e -> fichario.getConfiguracao().nova(Formulario.this));
				formularioAcao.setActionListener(e -> ConfigFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> ConfigDialogo.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_CONFIGURACAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuConexao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuConexao() {
				super(Constantes.LABEL_CONEXAO, Icones.BANCO);

				ficharioAcao.setActionListener(e -> fichario.getConexoes().nova(Formulario.this));
				formularioAcao.setActionListener(e -> ConexaoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> ConexaoDialogo.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_CONEXAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuFragmento extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuFragmento() {
				super(Constantes.LABEL_FRAGMENTO, Icones.FRAGMENTO);

				ficharioAcao.setActionListener(e -> fichario.getFragmento().novo(Formulario.this));
				formularioAcao.setActionListener(e -> FragmentoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> FragmentoDialogo.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_FRAGMENTO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuMapeamento extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuMapeamento() {
				super(Constantes.LABEL_MAPEAMENTOS, Icones.REFERENCIA);

				ficharioAcao.setActionListener(e -> fichario.getMapeamento().novo(Formulario.this));
				formularioAcao.setActionListener(e -> MapeamentoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> MapeamentoDialogo.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_MAPEAMENTO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuVariaveis extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuVariaveis() {
				super(Constantes.LABEL_VARIAVEIS, Icones.VAR);

				ficharioAcao.setActionListener(e -> fichario.getVariaveis().novo(Formulario.this));
				formularioAcao.setActionListener(e -> VariaveisFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> VariaveisDialogo.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_VARIAVEL)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		class MenuComparacao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuComparacao() {
				super(Constantes.LABEL_COMPARACAO, Icones.CENTRALIZAR);

				ficharioAcao.setActionListener(e -> fichario.getComparacao().nova(Formulario.this));
				formularioAcao.setActionListener(e -> ComparacaoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> ComparacaoDialogo.criar(Formulario.this));
			}

			void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_COMPARACAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
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