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
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.ambiente.AmbienteContainer;
import br.com.persist.ambiente.AmbienteDialogo;
import br.com.persist.ambiente.AmbienteFormulario;
import br.com.persist.anexo.AnexoTreeContainer;
import br.com.persist.anexo.AnexoTreeFormulario;
import br.com.persist.anotacao.AnotacaoDialogo;
import br.com.persist.anotacao.AnotacaoFormulario;
import br.com.persist.arquivo.ArquivoTreeContainer;
import br.com.persist.arquivo.ArquivoTreeFormulario;
import br.com.persist.comparacao.ComparacaoDialogo;
import br.com.persist.comparacao.ComparacaoFormulario;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.SplitPane;
import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoDialogo;
import br.com.persist.conexao.ConexaoFormulario;
import br.com.persist.conexao.ConexaoModelo;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.configuracao.ConfiguracaoDialogo;
import br.com.persist.configuracao.ConfiguracaoFormulario;
import br.com.persist.consulta.ConsultaDialogo;
import br.com.persist.consulta.ConsultaFormulario;
import br.com.persist.container.ContainerFormulario;
import br.com.persist.desktop.DesktopFormulario;
import br.com.persist.fichario.Fichario;
import br.com.persist.fragmento.FragmentoDialogo;
import br.com.persist.fragmento.FragmentoFormulario;
import br.com.persist.fragmento.FragmentoModelo;
import br.com.persist.icone.Icones;
import br.com.persist.macro.Macro;
import br.com.persist.mapeamento.MapeamentoDialogo;
import br.com.persist.mapeamento.MapeamentoFormulario;
import br.com.persist.mapeamento.MapeamentoModelo;
import br.com.persist.metadado.Metadado;
import br.com.persist.metadado.MetadadoTreeFormulario;
import br.com.persist.objeto.Objeto;
import br.com.persist.requisicao.RequisicaoDialogo;
import br.com.persist.requisicao.RequisicaoFormulario;
import br.com.persist.runtime_exec.RuntimeExecDialogo;
import br.com.persist.runtime_exec.RuntimeExecFormulario;
import br.com.persist.superficie.Superficie;
import br.com.persist.update.UpdateDialogo;
import br.com.persist.update.UpdateFormulario;
import br.com.persist.util.Action;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import br.com.persist.util.MenuPadrao1;
import br.com.persist.util.PosicaoDimensao;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.variaveis.VariaveisDialogo;
import br.com.persist.variaveis.VariaveisFormulario;
import br.com.persist.variaveis.VariaveisModelo;
import br.com.persist.xml.XML;
import br.com.persist.xml.XMLColetor;

public class Formulario extends JFrame implements ConexaoProvedor {
	private static final long serialVersionUID = 1L;
	private SplitPane splitPanePrincipal = Util.criarSplitPane(SplitPane.VERTICAL_SPLIT);
	private final transient List<Conexao> conexoes = new ArrayList<>();
	private final MenuPrincipal menuPrincipal = new MenuPrincipal();
	private static final Map<String, Object> map = new HashMap<>();
	private final transient Conteiner conteiner = new Conteiner();
	private final transient Arquivos arquivos = new Arquivos();
	private static final Logger LOG = Logger.getGlobal();
	private final Fichario fichario = new Fichario();
	public static final Macro macro = new Macro();

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
				iconeBandeja();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				menuPrincipal.fecharAcao.actionPerformed(null);
			}
		});
	}

	public void destacar(Conexao conexao, Superficie superficie, int tipoContainer, ConfigArquivo config) {
		fichario.getDestacar().destacar(this, conexao, superficie, tipoContainer, config);
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
			ContainerFormulario form = ContainerFormulario.criar(Formulario.this,
					new File(Mensagens.getString("label.abrir_exportacao")));
			form.abrirExportacaoImportacaoMetadado(metadado, true, circular);
			form.setLocationRelativeTo(Formulario.this);
			form.setVisible(true);
		}

		public void abrirImportacaoMetadado(Metadado metadado, boolean circular) {
			ContainerFormulario form = ContainerFormulario.criar(Formulario.this,
					new File(Mensagens.getString("label.abrir_importacao")));
			form.abrirExportacaoImportacaoMetadado(metadado, false, circular);
			form.setLocationRelativeTo(Formulario.this);
			form.setVisible(true);
		}

		public void exportarMetadadoRaiz(Metadado metadado) {
			if (metadado.getEhRaiz() && !metadado.estaVazio()) {
				ContainerFormulario form = ContainerFormulario.criar(Formulario.this,
						new File(Mensagens.getString("label.exportar")));
				form.exportarMetadadoRaiz(metadado);
				form.setLocationRelativeTo(Formulario.this);
				form.setVisible(true);
			}
		}
	}

	public class Arquivos {
		File arquivoParent;

		public void abrir(File file, boolean abrirNoFichario, ConfigArquivo config) {
			if (file == null || !file.isFile()) {
				return;
			}

			try {
				XMLColetor coletor = new XMLColetor();
				arquivoParent = file.getParentFile();
				XML.processar(file, coletor);

				if (abrirNoFichario) {
					fichario.getArquivos().abrir(Formulario.this, file, coletor, config);
				} else {
					abrir(Formulario.this, file, coletor, config);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, Formulario.this);
			}
		}

		public void abrir(Formulario formulario, File file, XMLColetor coletor, ConfigArquivo config) {
			ContainerFormulario form = ContainerFormulario.criar(formulario, file);
			form.abrir(file, coletor, getGraphics(), config);

			PosicaoDimensao pd = formulario.criarPosicaoDimensaoSeValido();

			if (pd != null) {
				form.setBounds(pd.getX(), pd.getY(), pd.getLargura(), pd.getAltura());
			} else {
				form.setLocationRelativeTo(formulario);
			}

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
		private final MenuRuntimeExec itemRuntimeExec = new MenuRuntimeExec();
		private final Menu menuUtil = new Menu(Constantes.LABEL_UTILITARIOS);
		private final Menu menuArquivo = new Menu(Constantes.LABEL_ARQUIVO);
		private final MenuMapeamento itemMapeamento = new MenuMapeamento();
		private final MenuComparacao itemComparacao = new MenuComparacao();
		private final MenuRequisicao itemRequisicao = new MenuRequisicao();
		private final Menu menuLAF = new Menu(Constantes.LABEL_APARENCIA);
		private final Menu menuBanco = new Menu(Constantes.LABEL_BANCO);
		private final Action fecharConnAcao = Action.actionMenuFechar();
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

		private MenuPrincipal() {
			FormularioUtil.menuAparencia(Formulario.this, menuLAF);

			menuArquivo.add(new MenuItem(novoAcao));
			menuArquivo.add(true, itemDesktop);
			menuArquivo.add(true, new MenuAbrir());
			menuArquivo.add(true, itemAnexo);
			menuArquivo.add(true, itemArquivo);
			menuArquivo.add(true, new MenuItem(fecharConnAcao));
			menuArquivo.add(new MenuItem(fecharAcao));
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
			menuUtil.add(true, itemRuntimeExec);
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

		private void abrirAutoFichario() {
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
			itemRuntimeExec.abrirAutoFichario();
			itemConfig.abrirAutoFichario();
		}

		private void eventos() {
			fecharConnAcao.rotulo("label.fechar_com_conexao");
			novoAcao.setActionListener(e -> fichario.getConteiner().novo(Formulario.this));

			fecharConnAcao.setActionListener(e -> fecharFormulario(true));
			fecharAcao.setActionListener(e -> fecharFormulario(false));
		}

		private List<MenuAmbiente> listaMenuAmbiente() {
			List<MenuAmbiente> lista = new ArrayList<>();

			for (AmbienteContainer.Ambiente ambiente : AmbienteContainer.Ambiente.values()) {
				lista.add(new MenuAmbiente(ambiente));
			}

			return lista;
		}

		private class MenuAmbiente extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuAmbiente(AmbienteContainer.Ambiente ambiente) {
				super(ambiente.getChaveLabel(), null);

				formularioAcao
						.setActionListener(e -> AmbienteFormulario.criar(Formulario.this, Constantes.VAZIO, ambiente));
				ficharioAcao.setActionListener(e -> fichario.getAmbientes().novo(Formulario.this, ambiente));
				dialogoAcao.setActionListener(e -> AmbienteDialogo.criar(Formulario.this, ambiente));
			}
		}

		private class MenuAnotacao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuAnotacao() {
				super(Constantes.LABEL_ANOTACOES, Icones.PANEL4);

				formularioAcao.setActionListener(e -> AnotacaoFormulario.criar(Formulario.this, Constantes.VAZIO));
				ficharioAcao.setActionListener(e -> fichario.getAnotacao().nova(Formulario.this));
				dialogoAcao.setActionListener(e -> AnotacaoDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_ANOTACAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuRequisicao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuRequisicao() {
				super(Constantes.LABEL_REQUISICAO, Icones.URL);

				formularioAcao
						.setActionListener(e -> RequisicaoFormulario.criar(Formulario.this, Constantes.VAZIO, null));
				ficharioAcao.setActionListener(e -> fichario.getRequisicao().nova(Formulario.this));
				dialogoAcao.setActionListener(e -> RequisicaoDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_REQUISICAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuRuntimeExec extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuRuntimeExec() {
				super(Constantes.LABEL_RUNTIME_EXEC, Icones.EXECUTAR);

				formularioAcao
						.setActionListener(e -> RuntimeExecFormulario.criar(Formulario.this, Constantes.VAZIO, null));
				ficharioAcao.setActionListener(e -> fichario.getRuntimeExec().novo(Formulario.this));
				dialogoAcao.setActionListener(e -> RuntimeExecDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_RUNTIME_EXEC)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuConsulta extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuConsulta() {
				super(Constantes.LABEL_CONSULTA, Icones.TABELA);

				formularioAcao
						.setActionListener(e -> ConsultaFormulario.criar(Formulario.this, Formulario.this, null, null));
				dialogoAcao.setActionListener(
						e -> ConsultaDialogo.criar(Formulario.this, Formulario.this, (Conexao) null));
				ficharioAcao.setActionListener(e -> fichario.getConsulta().nova(Formulario.this, null));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_CONSULTA)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuUpdate extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuUpdate() {
				super(Constantes.LABEL_ATUALIZAR, Icones.UPDATE);

				formularioAcao
						.setActionListener(e -> UpdateFormulario.criar(Formulario.this, Formulario.this, null, null));
				dialogoAcao
						.setActionListener(e -> UpdateDialogo.criar(Formulario.this, Formulario.this, (Conexao) null));
				ficharioAcao.setActionListener(e -> fichario.getUpdate().novo(Formulario.this, null));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_ATUALIZA)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuDesktop extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuDesktop() {
				super(Constantes.LABEL_DESKTOP, Icones.PANEL2, false);

				ficharioAcao.setActionListener(e -> fichario.getDesktops().novo(Formulario.this));
				formularioAcao.setActionListener(e -> DesktopFormulario.criar(Formulario.this));
			}
		}

		private class MenuArquivo extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuArquivo() {
				super(Constantes.LABEL_ARQUIVOS, Icones.EXPANDIR, false);

				ficharioAcao.setActionListener(e -> fichario.getArquivoTree().nova(Formulario.this));
				formularioAcao.setActionListener(e -> ArquivoTreeFormulario.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_ARQUIVO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuMetadado extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuMetadado() {
				super(Constantes.LABEL_METADADOS, Icones.CAMPOS, false);

				formularioAcao
						.setActionListener(e -> MetadadoTreeFormulario.criar(Formulario.this, Formulario.this, null));
				ficharioAcao.setActionListener(e -> fichario.getMetadadoTree().novo(Formulario.this, null));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_METADADO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuAnexo extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuAnexo() {
				super(Constantes.LABEL_ANEXOS, Icones.ANEXO, false);

				ficharioAcao.setActionListener(e -> fichario.getAnexoTree().novo(Formulario.this));
				formularioAcao.setActionListener(e -> AnexoTreeFormulario.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_ANEXO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuAbrir extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuAbrir() {
				super("label.abrir", Icones.ABRIR, false);

				formularioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
				ficharioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));

				eventos();
			}

			private void eventos() {
				formularioAcao.setActionListener(e -> {
					File[] files = getSelectedFiles(arquivos.arquivoParent, true);

					if (files == null || files.length == 0) {
						return;
					}

					for (File file : files) {
						arquivos.abrir(file, false, null);
					}
				});

				ficharioAcao.setActionListener(e -> {
					File[] files = getSelectedFiles(arquivos.arquivoParent, true);

					if (files == null || files.length == 0) {
						return;
					}

					for (File file : files) {
						arquivos.abrir(file, true, null);
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

		private class MenuLayout extends Menu {
			private static final long serialVersionUID = 1L;
			private Action arquivoAnexoEsquerdoAcao = Action.actionMenu("label.arquivo_anexo_esquerdo", null);
			private Action anexoArquivoEsquerdoAcao = Action.actionMenu("label.anexo_arquivo_esquerdo", null);
			private Action arquivoAnexoAbaixoAcao = Action.actionMenu("label.arquivo_anexo_abaixo", null);
			private Action anexoArquivoAbaixoAcao = Action.actionMenu("label.anexo_arquivo_abaixo", null);
			private Action somenteFicharioAcao = Action.actionMenu("label.somente_fichario", null);
			private Action arquivoAbaixoAcao = Action.actionMenu("label.arquivo_abaixo", null);
			private Action anexoAbaixoAcao = Action.actionMenu("label.anexo_abaixo", null);

			private MenuLayout() {
				super("label.layout", Icones.REGION);
				addMenuItem(somenteFicharioAcao);
				addMenuItem(arquivoAnexoEsquerdoAcao);
				addMenuItem(anexoArquivoEsquerdoAcao);
				addMenuItem(arquivoAnexoAbaixoAcao);
				addMenuItem(anexoArquivoAbaixoAcao);
				addMenuItem(arquivoAbaixoAcao);
				addMenuItem(anexoAbaixoAcao);

				somenteFicharioAcao.setActionListener(e -> somenteFichario());
				arquivoAnexoEsquerdoAcao.setActionListener(e -> arquivoAnexoEsquerdo());
				anexoArquivoEsquerdoAcao.setActionListener(e -> anexoArquivoEsquerdo());
				arquivoAnexoAbaixoAcao.setActionListener(e -> arquivoAnexoAbaixo());
				anexoArquivoAbaixoAcao.setActionListener(e -> anexoArquivoAbaixo());
				arquivoAbaixoAcao.setActionListener(e -> arquivoAbaixo());
				anexoAbaixoAcao.setActionListener(e -> anexoAbaixo());
			}

			private void somenteFichario() {
				Formulario.this.remove(splitPanePrincipal);
				Formulario.this.remove(fichario);

				Formulario.this.add(BorderLayout.CENTER, fichario);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void arquivoAnexoEsquerdo() {
				Dimension sizePrincipal = Formulario.this.getSize();
				Formulario.this.remove(splitPanePrincipal);
				Formulario.this.remove(fichario);

				ArquivoTreeContainer arquivoTree = new ArquivoTreeContainer(null, Formulario.this);
				AnexoTreeContainer anexoTree = new AnexoTreeContainer(null, Formulario.this);
				SplitPane splitEsquerdo = Util.splitPaneVertical(arquivoTree, anexoTree, sizePrincipal.height / 2);
				splitPanePrincipal = Util.splitPaneHorizontal(splitEsquerdo, fichario, sizePrincipal.width / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPanePrincipal);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void anexoArquivoEsquerdo() {
				Dimension sizePrincipal = Formulario.this.getSize();
				Formulario.this.remove(splitPanePrincipal);
				Formulario.this.remove(fichario);

				ArquivoTreeContainer arquivoTree = new ArquivoTreeContainer(null, Formulario.this);
				AnexoTreeContainer anexoTree = new AnexoTreeContainer(null, Formulario.this);
				SplitPane splitEsquerdo = Util.splitPaneVertical(anexoTree, arquivoTree, sizePrincipal.height / 2);
				splitPanePrincipal = Util.splitPaneHorizontal(splitEsquerdo, fichario, sizePrincipal.width / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPanePrincipal);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void arquivoAnexoAbaixo() {
				Dimension sizePrincipal = Formulario.this.getSize();
				Formulario.this.remove(splitPanePrincipal);
				Formulario.this.remove(fichario);

				ArquivoTreeContainer arquivoTree = new ArquivoTreeContainer(null, Formulario.this);
				AnexoTreeContainer anexoTree = new AnexoTreeContainer(null, Formulario.this);
				SplitPane splitAbaixo = Util.splitPaneHorizontal(arquivoTree, anexoTree, sizePrincipal.width / 2);
				splitPanePrincipal = Util.splitPaneVertical(fichario, splitAbaixo, sizePrincipal.height / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPanePrincipal);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void arquivoAbaixo() {
				Dimension sizePrincipal = Formulario.this.getSize();
				Formulario.this.remove(splitPanePrincipal);
				Formulario.this.remove(fichario);

				ArquivoTreeContainer arquivoTree = new ArquivoTreeContainer(null, Formulario.this);
				splitPanePrincipal = Util.splitPaneVertical(fichario, arquivoTree, sizePrincipal.height / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPanePrincipal);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void anexoArquivoAbaixo() {
				Dimension sizePrincipal = Formulario.this.getSize();
				Formulario.this.remove(splitPanePrincipal);
				Formulario.this.remove(fichario);

				ArquivoTreeContainer arquivoTree = new ArquivoTreeContainer(null, Formulario.this);
				AnexoTreeContainer anexoTree = new AnexoTreeContainer(null, Formulario.this);
				SplitPane splitAbaixo = Util.splitPaneHorizontal(anexoTree, arquivoTree, sizePrincipal.width / 2);
				splitPanePrincipal = Util.splitPaneVertical(fichario, splitAbaixo, sizePrincipal.height / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPanePrincipal);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void anexoAbaixo() {
				Dimension sizePrincipal = Formulario.this.getSize();
				Formulario.this.remove(splitPanePrincipal);
				Formulario.this.remove(fichario);

				AnexoTreeContainer anexoTree = new AnexoTreeContainer(null, Formulario.this);
				splitPanePrincipal = Util.splitPaneVertical(fichario, anexoTree, sizePrincipal.height / 2);
				Formulario.this.add(BorderLayout.CENTER, splitPanePrincipal);
				SwingUtilities.updateComponentTreeUI(Formulario.this);
			}

			private void aplicarLayout() {
				int valor = Preferencias.getLayoutAbertura();

				if (valor == 1) {
					somenteFichario();
				} else if (valor == 2) {
					arquivoAnexoEsquerdo();
				} else if (valor == 3) {
					anexoArquivoEsquerdo();
				} else if (valor == 4) {
					arquivoAnexoAbaixo();
				} else if (valor == 5) {
					anexoArquivoAbaixo();
				} else if (valor == 6) {
					arquivoAbaixo();
				} else if (valor == 7) {
					anexoAbaixo();
				}
			}
		}

		private class MenuConfig extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;
			private Action exportarAcao = Action.actionMenu("label.exportar", Icones.TOP);
			private Action importarAcao = Action.actionMenu("label.importar", Icones.BAIXAR2);

			private MenuConfig() {
				super(Constantes.LABEL_CONFIGURACOES, Icones.CONFIG);

				addSeparator();
				addMenuItem(exportarAcao);
				addMenuItem(importarAcao);

				exportarAcao.setActionListener(e -> exportar());
				importarAcao.setActionListener(e -> importar());

				ficharioAcao.setActionListener(e -> fichario.getConfiguracao().nova(Formulario.this));
				formularioAcao.setActionListener(e -> ConfiguracaoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> ConfiguracaoDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_CONFIGURACAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}

			private void exportar() {
				try {
					Preferencias.exportar();
					Util.mensagem(this, "SUCESSO");
				} catch (Exception ex) {
					Util.stackTraceAndMessage(getClass().getName(), ex, this);
				}
			}

			private void importar() {
				try {
					Preferencias.importar();
					Util.mensagem(this, "SUCESSO");
				} catch (Exception ex) {
					Util.stackTraceAndMessage(getClass().getName(), ex, this);
				}
			}
		}

		private class MenuConexao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuConexao() {
				super(Constantes.LABEL_CONEXAO, Icones.BANCO);

				ficharioAcao.setActionListener(e -> fichario.getConexoes().nova(Formulario.this));
				formularioAcao.setActionListener(e -> ConexaoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> ConexaoDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_CONEXAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuFragmento extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuFragmento() {
				super(Constantes.LABEL_FRAGMENTO, Icones.FRAGMENTO);

				ficharioAcao.setActionListener(e -> fichario.getFragmento().novo(Formulario.this));
				formularioAcao.setActionListener(e -> FragmentoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> FragmentoDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_FRAGMENTO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuMapeamento extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuMapeamento() {
				super(Constantes.LABEL_MAPEAMENTOS, Icones.REFERENCIA);

				ficharioAcao.setActionListener(e -> fichario.getMapeamento().novo(Formulario.this));
				formularioAcao.setActionListener(e -> MapeamentoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> MapeamentoDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_MAPEAMENTO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuVariaveis extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuVariaveis() {
				super(Constantes.LABEL_VARIAVEIS, Icones.VAR);

				ficharioAcao.setActionListener(e -> fichario.getVariaveis().novo(Formulario.this));
				formularioAcao.setActionListener(e -> VariaveisFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> VariaveisDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_VARIAVEL)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}

		private class MenuComparacao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			private MenuComparacao() {
				super(Constantes.LABEL_COMPARACAO, Icones.CENTRALIZAR);

				ficharioAcao.setActionListener(e -> fichario.getComparacao().nova(Formulario.this));
				formularioAcao.setActionListener(e -> ComparacaoFormulario.criar(Formulario.this));
				dialogoAcao.setActionListener(e -> ComparacaoDialogo.criar(Formulario.this));
			}

			private void abrirAutoFichario() {
				if (Preferencias.getBoolean(Constantes.ABRIR_AUTO_FICHARIO_COMPARACAO)) {
					ficharioAcao.actionPerformed(null);
				}
			}
		}
	}

	private void fecharFormulario(boolean fecharConexao) {
		if (Util.confirmar(Formulario.this, "label.confirma_fechar")) {
			Preferencias.setFecharConexao(fecharConexao);
			FormularioUtil.fechar(Formulario.this);
			System.exit(0);
		}
	}

	private void iconeBandeja() {
		PopupMenu popup = new PopupMenu();

		java.awt.MenuItem itemFechar = new java.awt.MenuItem(Mensagens.getString(Constantes.LABEL_FECHAR));
		itemFechar.addActionListener(e -> fecharFormulario(false));
		popup.add(itemFechar);

		URL url = getClass().getResource(Constantes.IMAGEM_TRAY_ICON);
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		SystemTray systemTray = SystemTray.getSystemTray();

		TrayIcon trayIcon = new TrayIcon(image, Mensagens.getTituloAplicacao(), popup);
		trayIcon.setImageAutoSize(true);

		try {
			systemTray.add(trayIcon);
			trayIcon.displayMessage(Mensagens.getTituloAplicacao(), Mensagens.getString("label.descricao_aplicacao"),
					TrayIcon.MessageType.INFO);
			trayIcon.addActionListener(new IconeBandejaListener());
		} catch (AWTException ex) {
			LOG.log(Level.SEVERE, Constantes.ERRO, ex);
		}
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

		if (principalLocation.y < 100 && !Util.porcentagemMaiorQue(principalSize.height, configuraSize.height, 70)) {
			int x = principalLocation.x;
			int y = principalLocation.y + principalSize.height + espaco;
			int l = principalSize.width;
			int a = configuraSize.height - principalSize.height - espaco;
			return new PosicaoDimensao(x, y, l, a);

		} else if (principalLocation.x < 100
				&& !Util.porcentagemMaiorQue(principalSize.width, configuraSize.width, 70)) {
			int x = principalLocation.x + principalSize.width + espaco;
			int y = principalLocation.y;
			int l = configuraSize.width - principalSize.width - espaco;
			int a = principalSize.height;
			return new PosicaoDimensao(x, y, l, a);
		}

		return null;
	}
}