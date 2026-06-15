package br.com.persist.plugins.projeto;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoTreeUtil;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SplitPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextField;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLHandler;
import br.com.persist.marca.XMLUtil;
import br.com.persist.painel.Fichario;
import br.com.persist.painel.Root;
import br.com.persist.painel.Separador;
import br.com.persist.painel.SeparadorException;
import br.com.persist.painel.Transferivel;
import br.com.persist.plugins.expressao.biblionativo.Linha;
import br.com.persist.plugins.expressao.biblionativo.Lista;
import br.com.persist.plugins.expressao.biblionativo.NArquivo;

class ProjetoSplit extends SplitPane {
	private Action atualizarAction = Action.acaoMenu(ProjetoMensagens.getString("label.atualizar"), Icones.ATUALIZAR);
	private Action sufixosAction = Action.acaoMenu(ProjetoMensagens.getString("label.sufixos"), null);
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final File fileRoot;
	ProjetoContainer container;
	private ArquivoTree tree;
	private PanelRoot panel;

	ProjetoSplit() {
		super(HORIZONTAL_SPLIT);
		fileRoot = new File(ProjetoConstantes.PROJETOS);
	}

	void inicializar(ProjetoContainer container) {
		this.container = container;
		File file = new File(fileRoot, ProjetoConstantes.IGNORADOS);
		List<String> ignorados = ArquivoUtil.getIgnorados(file);
		ArquivoUtil.arquivoIgnorado(ignorados, ProjetoPreferencia.isExibirArqIgnorados());
		Arquivo raiz = new Arquivo(fileRoot, ignorados);
		tree = new ArquivoTree(new ArquivoModelo(raiz));
		tree.getArquivoPopup().addMenuItem(atualizarAction);
		atualizarAction.setActionListener(this::atualizar);
		tree.getArquivoPopup().addMenuItem(sufixosAction);
		sufixosAction.setActionListener(this::sufixos);
		tree.setCellRenderer(new ProjetoRenderer());
		setLeftComponent(new ScrollPane(tree));
		tree.adicionarOuvinte(treeListener);
		panel = new PanelRoot();
		setRightComponent(panel);
		panel.tree = tree;
		abrir();
	}

	private void atualizar(ActionEvent e) {
		Arquivo arquivo = tree.getObjetoSelecionado();
		if (arquivo != null) {
			List<TreePath> expandidos = tree.getExpandidos(arquivo);
			arquivo.atualizarEstrutura();
			ArquivoTreeUtil.atualizarEstrutura(tree, arquivo, expandidos);
		}
	}

	private void sufixos(ActionEvent e) {
		ProjetoSufixoDialogo.view(ProjetoSplit.this);
	}

	void salvar() throws XMLException {
		File file = new File(fileRoot, "hierarquia.xml");
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2("projeto");
		panel.salvar(util);
		util.finalizarTag("projeto");
		util.close();
	}

	void abrir() {
		File file = new File(fileRoot, "hierarquia.xml");
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new ProjetoHandler(panel, tree.getModelo()));
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	void abrir(Arquivo arquivo) throws ProjetoException {
		if (arquivo == null) {
			return;
		}
		Fichario fichario = panel.getFicharioSelecionado();
		if (fichario != null) {
			novaAba(fichario, arquivo);
		} else {
			fichario = panel.getFicharioPrimeiro();
			if (fichario != null) {
				novaAba(fichario, arquivo);
			} else {
				fichario = novoFichario(arquivo);
				panel.setRoot(fichario);
			}
		}
		SwingUtilities.updateComponentTreeUI(panel);
	}

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		tree.contemConteudo(set, string, porParte);
	}

	private Fichario novoFichario(Arquivo arquivo) {
		Fichario fichario = new Fichario();
		fichario.setTabPlacement(ProjetoPreferencia.getProjetoPosicaoAbaFichario());
		novaAba(fichario, arquivo);
		return fichario;
	}

	public static void novaAba(Fichario fichario, Arquivo arquivo) {
		fichario.addTab(arquivo.getName(), new Aba(arquivo));
		int indice = fichario.getTabCount() - 1;
		fichario.setToolTipTextAt(indice, arquivo.getFile().getAbsolutePath());
		fichario.setSelectedIndex(indice);
	}

	public static void novaAba(Fichario fichario, File file) {
		fichario.addTab(file.getName(), new Aba(file));
		int indice = fichario.getTabCount() - 1;
		fichario.setToolTipTextAt(indice, file.getAbsolutePath());
		fichario.setSelectedIndex(indice);
	}

	public ArquivoTree getTree() {
		return tree;
	}

	private transient ArquivoTreeListener treeListener = new ArquivoTreeListener() {
		@Override
		public void focusInputPesquisar(ArquivoTree arquivoTree) {
			if (container != null) {
				container.focusInputPesquisarTree();
			}
		}

		@Override
		public void renomearArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				List<TreePath> expandidos = arquivoTree.getExpandidos(arquivo);
				String nome = ArquivoUtil.getNome(ProjetoSplit.this, arquivo.getName());
				if (nome != null && arquivo.renomear(nome)) {
					ArquivoTreeUtil.atualizarEstrutura(arquivoTree, arquivo, expandidos);
					panel.renomear();
				}
			}
		}

		@Override
		public void excluirArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null && Util.confirmar(ProjetoSplit.this, "msg.confirma_exclusao")) {
				arquivo.excluir();
				ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
				try {
					panel.excluir(arquivo);
				} catch (ProjetoException | SeparadorException ex) {
					Util.mensagem(ProjetoSplit.this, ex.getMessage());
				}
			}
		}

		@Override
		public void clonarEmArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				try {
					AtomicReference<File> ref = new AtomicReference<>();
					String resp = Util.clonarEm(ProjetoSplit.this, arquivo.getFile(), ref);
					if (Preferencias.isExibirTotalBytesClonados()) {
						Util.mensagem(ProjetoSplit.this, resp);
					}
				} catch (IOException e) {
					Util.mensagem(ProjetoSplit.this, e.getMessage());
				}
			}
		}

		@Override
		public void clonarArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				clonar(arquivoTree, arquivo);
			}
		}

		private void clonar(ArquivoTree arquivoTree, Arquivo arquivo) {
			try {
				AtomicReference<File> ref = new AtomicReference<>();
				String resp = Util.clonar(ProjetoSplit.this, arquivo.getFile(), ref);
				if (Preferencias.isExibirTotalBytesClonados()) {
					Util.mensagem(ProjetoSplit.this, resp);
				}
				adicionar(arquivoTree, arquivo.getPai(), ref.get());
			} catch (IOException e) {
				Util.mensagem(ProjetoSplit.this, e.getMessage());
			}
		}

		@Override
		public void diretorioArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null) {
				desktopOpen(arquivo);
			}
		}

		private void desktopOpen(Arquivo arquivo) {
			try {
				ArquivoUtil.diretorio(arquivo.getFile());
			} catch (IOException e) {
				Util.mensagem(ProjetoSplit.this, e.getMessage());
			}
		}

		@Override
		public void abrirArquivo(ArquivoTree arquivoTree) {
			try {
				abrir(arquivoTree.getObjetoSelecionado());
			} catch (ProjetoException ex) {
				Util.mensagem(ProjetoSplit.this, ex.getMessage());
			}
		}

		@Override
		public void novoDiretorio(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoDiretorio(ProjetoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		@Override
		public void novoArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (valido(arquivo)) {
				File file = ArquivoUtil.novoArquivo(ProjetoSplit.this, arquivo.getFile());
				adicionar(arquivoTree, arquivo, file);
			}
		}

		private boolean valido(Arquivo arquivo) {
			return arquivo != null && arquivo.isDirectory();
		}

		private void adicionar(ArquivoTree arquivoTree, Arquivo arquivo, File file) {
			if (file != null && arquivo != null) {
				Arquivo novo = arquivo.adicionar(file);
				if (novo != null) {
					arquivo.ordenar();
					ArquivoTreeUtil.atualizarEstrutura(arquivoTree, arquivo);
					ArquivoTreeUtil.selecionarObjeto(arquivoTree, novo);
					arquivoTree.requestFocus();
					arquivoTree.repaint();
				}
			}
		}
	};
}

class Editor extends TextEditor {
	private static final long serialVersionUID = 1L;

	Editor() {
		addFocusListener(focusListenerInner);
	}

	private transient FocusListener focusListenerInner = new FocusAdapter() {
		@Override
		public void focusGained(java.awt.event.FocusEvent e) {
			Component c = getParent();
			while (c != null) {
				if (c instanceof Fichario) {
					Fichario.setSelecionado((Fichario) c);
					break;
				}
				c = c.getParent();
			}
		}
	};
}

class Aba extends Transferivel implements ItemListener {
	private final JComboBox<String> comboEnderecosAbsolutos;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final Editor editor = new Editor();
	final transient Arquivo arquivo;

	Aba(Arquivo arquivo) {
		comboEnderecosAbsolutos = Formulario.criarComboEnderecosAbsolutos();
		this.arquivo = Objects.requireNonNull(arquivo);
		comboEnderecosAbsolutos.addItemListener(this);
		toolbar.ini();
		montarLayout();
		abrir();
	}

	Aba(File file) {
		toolbar.ini(Mensagens.getString("msg.arquivo_inexistente") + " " + file.getAbsolutePath());
		add(BorderLayout.NORTH, toolbar);
		comboEnderecosAbsolutos = null;
		this.arquivo = null;
	}

	@Override
	public boolean associadoA(File file) {
		return arquivo.getFile().equals(file);
	}

	public static String getStringRelativo(File base, File file) {
		String absolutoBase = base.getAbsolutePath();
		String absolutoFile = file.getAbsolutePath();
		if (absolutoFile.startsWith(absolutoBase)) {
			int length = absolutoBase.length();
			String nome = absolutoFile.substring(length + 1);
			return Util.replaceAll(nome, Constantes.SEPARADOR, Constantes.SEP);
		}
		return file.getAbsolutePath();
	}

	@Override
	public String getStringFile() {
		return getStringRelativo(new File(ProjetoConstantes.PROJETOS), arquivo.getFile());
	}

	@Override
	public File getFile() {
		return arquivo.getFile();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		JScrollPane scrollPane = new JScrollPane(editor);
		scrollPane.setRowHeaderView(new TextEditorLine(editor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		editor.setListener(
				TextEditor.newTextEditorAdapter(toolbar::focusInputPesquisar, toolbar::salvar, toolbar::baixar));
		add(BorderLayout.SOUTH, comboEnderecosAbsolutos);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {
			Object selecionado = comboEnderecosAbsolutos.getSelectedItem();
			if (selecionado instanceof String) {
				toolbar.txtArquivo.setText(selecionado.toString());
			}
		}
	}

	private void abrir() {
		editor.limpar();
		if (arquivo.getFile().exists()) {
			try {
				editor.setText(ArquivoUtil.getString(arquivo.getFile()));
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}
	}

	@Override
	public void processar(Fichario fichario, int indice, Map<String, Object> map) {
		if (map.containsKey(Transferivel.RENOMEAR)) {
			fichario.setTitleAt(indice, arquivo.getName());
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final String ERRO_FRAGMENTO_INEXISTENTE = "erro.fragmento_inexistente";
		private static final String ERRO_ARQUIVO_INEXISTENTE = "erro.arquivo_inexistente";
		private static final String ERRO_INICIO_GT_FIM = "erro.inicio_gt_fim";
		private static final String ERRO_INICIO_GE_FIM = "erro.inicio_ge_fim";
		private String fragmentoArquivo = "fragmento_arquivo";
		private String fragmentoInicio = "fragmento_inicio";
		private String fragmentoFinal = "fragmento_final";
		private TextField txtArquivo = new TextField(25);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini() {
			super.ini(new Nil(), LIMPAR, BAIXAR, SALVAR, COPIAR, COLAR);
			limparAcao.text(ProjetoMensagens.getString("label.limpar_inicializar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
			add(txtArquivo);
			txtArquivo.setToolTipText(ProjetoMensagens.getString("label.tooltip_txt_arquivo"));

			Action selArquivoAction = Action.acaoMenu(Mensagens.getString("label.arquivo"), Icones.ABRIR);
			selArquivoAction.setActionListener(e -> selArquivo());
			addButton(selArquivoAction);

			Action lerArquivoAction = Action.acaoMenu(ProjetoMensagens.getString("label.ler_arquivo"),
					Icones.SINCRONIZAR);
			lerArquivoAction.setActionListener(e -> lerArquivo());
			addButton(lerArquivoAction);

			Action lerFragmentoAction = Action.acaoMenu(ProjetoMensagens.getString("label.ler_fragmento"),
					Icones.ATUALIZAR);
			lerFragmentoAction.setActionListener(e -> lerFragmento());
			addButton(lerFragmentoAction);

			Action renomearAction = Action.acaoMenu(ProjetoMensagens.getString("label.renomear"), Icones.RULE);
			renomearAction.setActionListener(e -> renomear());
			addButton(renomearAction);
		}

		public void ini(String arqAbsoluto) {
			label.setText(arqAbsoluto);
			add(label);
		}

		private void selArquivo() {
			checarInputArquivo();
			if (Util.isEmpty(txtArquivo.getText())) {
				txtArquivo.setText(ProjetoPreferencia.getDirPadraoSelecaoArquivos());
			}
			JFileChooser fileChooser = new JFileChooser(ArquivoUtil.getValido(txtArquivo.getText()));
			int i = fileChooser.showOpenDialog(Aba.this);
			if (i == JFileChooser.APPROVE_OPTION) {
				File sel = fileChooser.getSelectedFile();
				if (sel != null) {
					txtArquivo.setText(sel.getAbsolutePath());
				}
			}
		}

		private void checarInputArquivo() {
			if (Util.isEmpty(txtArquivo.getText())) {
				String conteudoEditor = editor.getText();
				if (Util.isEmpty(conteudoEditor)) {
					return;
				}
				try {
					File arquivoFragmento = getArquivo(conteudoEditor, fragmentoArquivo);
					txtArquivo.setText(arquivoFragmento.getAbsolutePath());
				} catch (Exception ex) {
					//
				}
			}
		}

		private void lerArquivo() {
			checarInputArquivo();
			if (Util.isEmpty(txtArquivo.getText())) {
				Util.mensagem(Aba.this, ProjetoMensagens.getString("erro.input_arquivo_vazio"));
				return;
			}
			File file = new File(txtArquivo.getText().trim());
			if (!file.isFile()) {
				Util.mensagem(Aba.this, ProjetoMensagens.getString(ERRO_ARQUIVO_INEXISTENTE, file.getAbsolutePath()));
				return;
			}
			try {
				String conteudo = ArquivoUtil.getString(file);
				if (Util.confirmar(this, ProjetoMensagens.getString("msg.arquivo_no_editor"), false)) {
					editor.setText(conteudo);
				} else {
					Util.mensagemFormulario(Aba.this, conteudo);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}

		private void lerFragmento() {
			String conteudoEditor = editor.getText();
			if (Util.isEmpty(conteudoEditor)) {
				Util.mensagem(Aba.this, ProjetoMensagens.getString("erro.editor_vazio"));
				return;
			}

			File arquivoFragmento = null;
			try {
				arquivoFragmento = getArquivo(conteudoEditor, fragmentoArquivo);
			} catch (Exception ex) {
				Util.mensagem(Aba.this, ex.getMessage());
				return;
			}

			int inicio = -1;
			try {
				inicio = getInteiro(conteudoEditor, fragmentoInicio);
			} catch (Exception ex) {
				Util.mensagem(Aba.this, ex.getMessage());
				return;
			}

			int fim = -1;
			try {
				fim = getInteiro(conteudoEditor, fragmentoFinal);
			} catch (Exception ex) {
				Util.mensagem(Aba.this, ex.getMessage());
				return;
			}

			if (inicio > fim) {
				Util.mensagem(Aba.this, ProjetoMensagens.getString(ERRO_INICIO_GT_FIM));
				return;
			}

			Lista lista = null;
			try {
				br.com.persist.plugins.expressao.biblionativo.Arquivo nArquivo = NArquivo
						.criarArquivo(arquivoFragmento.getAbsolutePath());
				lista = nArquivo.getLista();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
				return;
			}

			if (lista.size().longValue() == 0) {
				Util.mensagem(Aba.this,
						ProjetoMensagens.getString("erro.arquivo_sem_conteudo", arquivoFragmento.getAbsolutePath()));
				return;
			}

			if (fim > lista.size().longValue()) {
				Util.mensagem(Aba.this, ProjetoMensagens.getString("erro.fim_maior_conteudo", String.valueOf(fim)));
				return;
			}

			try {
				List<Linha> linhas = new ArrayList<>();
				for (int i = inicio - 1; i < fim; i++) {
					linhas.add((Linha) lista.get(i));
				}
				StringBuilder builder = new StringBuilder();
				append(builder, fragmentoArquivo, arquivoFragmento.getAbsolutePath());
				append(builder, fragmentoInicio, inicio + "");
				append(builder, fragmentoFinal, fim + "");
				builder.append(Constantes.QL + getString(linhas));
				if (Util.confirmar(this, ProjetoMensagens.getString("msg.arquivo_no_editor"), false)) {
					editor.setText(builder.toString());
				} else {
					Util.mensagemFormulario(Aba.this, builder.toString());
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}

		private String getString(List<Linha> linhas) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			for (Linha item : linhas) {
				item.print(pw);
			}
			return Util.replaceCR(sw.toString());
		}

		private void append(StringBuilder builder, String fragmento, String valor) {
			builder.append(fragmento + ">" + valor + "<" + fragmento + Constantes.QL);
		}

		private void renomear() {
			String conteudoEditor = editor.getText();
			if (Util.isEmpty(conteudoEditor)) {
				Util.mensagem(Aba.this, ProjetoMensagens.getString("erro.editor_vazio"));
				return;
			}

			File arquivoFragmento = null;
			try {
				arquivoFragmento = getArquivo(conteudoEditor, fragmentoArquivo);
			} catch (Exception ex) {
				Util.mensagem(Aba.this, ex.getMessage());
				return;
			}

			String nome = ArquivoUtil.getNome(Aba.this, arquivoFragmento.getName());
			if (nome != null && arquivo != null && arquivo.renomear(nome)) {
				Fichario fichario = getFichario();
				if (fichario != null) {
					Map<String, Object> map = new HashMap<>();
					map.put(Transferivel.RENOMEAR, null);
					fichario.processar(map);
				}
			}
		}

		private File getArquivo(String conteudo, String tag) throws ProjetoException {
			String string = tag + ">";
			int posIni = conteudo.indexOf(string);
			if (posIni == -1) {
				throw new ProjetoException(ERRO_FRAGMENTO_INEXISTENTE, string);
			}
			string = "<" + tag;
			int posFim = conteudo.indexOf(string);
			if (posFim == -1) {
				throw new ProjetoException(ERRO_FRAGMENTO_INEXISTENTE, string);
			}
			if (posIni >= posFim) {
				throw new ProjetoException(ERRO_INICIO_GE_FIM);
			}
			String absoluto = conteudo.substring(posIni + string.length(), posFim).trim();
			File file = new File(absoluto);
			if (!file.isFile()) {
				throw new ProjetoException(ERRO_ARQUIVO_INEXISTENTE, file.getAbsolutePath());
			}
			return file;
		}

		private int getInteiro(String conteudo, String tag) throws ProjetoException {
			String string = tag + ">";
			int posIni = conteudo.indexOf(string);
			if (posIni == -1) {
				throw new ProjetoException(ERRO_FRAGMENTO_INEXISTENTE, string);
			}
			string = "<" + tag;
			int posFim = conteudo.indexOf(string);
			if (posFim == -1) {
				throw new ProjetoException(ERRO_FRAGMENTO_INEXISTENTE, string);
			}
			if (posIni >= posFim) {
				throw new ProjetoException(ERRO_INICIO_GE_FIM);
			}
			String valor = conteudo.substring(posIni + string.length(), posFim).trim();
			int resp = -1;
			try {
				resp = Integer.parseInt(valor);
			} catch (Exception ex) {
				throw new ProjetoException("erro.conversao_valor", valor);
			}
			if (resp < 1) {
				throw new ProjetoException("erro.indice_inferior_invalido", resp);
			}
			return resp;
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		protected void limpar() {
			if (Util.isEmpty(txtArquivo.getText())) {
				editor.limpar();
				return;
			}
			String conteudo = editor.getText();
			StringBuilder builder = new StringBuilder();
			append(builder, fragmentoArquivo, txtArquivo.getText().trim());
			append(builder, fragmentoInicio, "1");
			append(builder, fragmentoFinal, "1");
			builder.append(Constantes.QL);
			if (!Util.isEmpty(conteudo)
					&& Util.confirmar(this, ProjetoMensagens.getString("msg.manter_conteudo_editor"), false)) {
				builder.append(conteudo);
			}
			editor.setText(builder.toString());
		}

		@Override
		protected void copiar() {
			String string = Util.getString(editor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			editor.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(editor, numeros, letras);
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(Aba.this)) {
				salvarArquivo(arquivo.getFile());
			}
		}

		private void salvarArquivo(File file) {
			try {
				ArquivoUtil.salvar(editor, file);
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(editor, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}
}

class PanelRoot extends Panel implements Root {
	private static final long serialVersionUID = 1L;
	ArquivoTree tree;

	void salvar(XMLUtil util) {
		if (getComponentCount() == 0) {
			return;
		}
		if (getComponent(0) instanceof Fichario) {
			((Fichario) getComponent(0)).salvar(util);
		}
		if (getComponent(0) instanceof Separador) {
			((Separador) getComponent(0)).salvar(util);
		}
	}

	Fichario getFicharioSelecionado() {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario) {
			return (Fichario) getComponent(0);
		}
		return ((Separador) getComponent(0)).getFicharioSelecionado();
	}

	Fichario getFicharioPrimeiro() {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario) {
			return (Fichario) getComponent(0);
		}
		return ((Separador) getComponent(0)).getFicharioPrimeiro();
	}

	Transferivel getTransferivel(File file) {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario) {
			return ((Fichario) getComponent(0)).getTransferivel(file);
		}
		return ((Separador) getComponent(0)).getTransferivel(file);
	}

	Fichario getFichario(Transferivel objeto) {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario && ((Fichario) getComponent(0)).contem(objeto)) {
			return (Fichario) getComponent(0);
		}
		if (getComponent(0) instanceof Separador) {
			return ((Separador) getComponent(0)).getFichario(objeto);
		}
		return null;
	}

	void setRootIf(Component c) {
		if (getComponentCount() == 0) {
			add(c);
		}
	}

	void setRoot(Component c) throws ProjetoException {
		if (getComponentCount() > 0) {
			throw new ProjetoException("getComponentCount() > 0", false);
		}
		add(c);
	}

	void renomear() {
		if (getComponentCount() == 0) {
			return;
		}
		Map<String, Object> map = new HashMap<>();
		map.put(Transferivel.RENOMEAR, null);
		if (getComponent(0) instanceof Fichario) {
			((Fichario) getComponent(0)).processar(map);
			return;
		}
		((Separador) getComponent(0)).processar(map);
	}

	void excluir(Arquivo arquivo) throws ProjetoException, SeparadorException {
		Transferivel objeto = getTransferivel(arquivo.getFile());
		while (objeto != null) {
			Fichario fichario = getFichario(objeto);
			if (fichario != null) {
				if (!fichario.contem(objeto)) {
					throw new ProjetoException("!fichario.contem(objeto)", false);
				} else {
					fichario.excluir(objeto);
				}
			} else {
				throw new ProjetoException("fichario == null", false);
			}
			objeto = getTransferivel(arquivo.getFile());
		}
	}

	@Override
	public void abaSelecionada(Fichario fichario, Transferivel transferivel) {
		if (transferivel instanceof Aba) {
			Aba aba = (Aba) transferivel;
			if (aba.arquivo != null) {
				ArquivoTreeUtil.selecionarObjeto(tree, aba.arquivo);
			}
		}
	}
}

class ProjetoHandler extends XMLHandler {
	private final ArquivoModelo modelo;
	private final PanelRoot root;
	Separador separador;
	Fichario fichario;

	ProjetoHandler(PanelRoot root, ArquivoModelo modelo) {
		this.modelo = modelo;
		this.root = root;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fichario".equals(qName)) {
			fichario = new Fichario();
			fichario.setTabPlacement(ProjetoPreferencia.getProjetoPosicaoAbaFichario());
			root.setRootIf(fichario);
			setComponent(separador, fichario);
		} else if ("separador".equals(qName)) {
			int orientation = Integer.parseInt(attributes.getValue("orientacao"));
			Separador separadorBkp = separador;
			separador = new Separador(orientation, null, null);
			root.setRootIf(separador);
			setComponent(separadorBkp, separador);
		} else if ("transferivel".equals(qName)) {
			String nome = attributes.getValue("file");
			nome = Util.replaceAll(nome, Constantes.SEP, Constantes.SEPARADOR);
			File fileRoot = new File(ProjetoConstantes.PROJETOS);
			File file = new File(fileRoot, nome);
			Arquivo arquivo = modelo.getArquivo(file);
			if (arquivo != null) {
				ProjetoSplit.novaAba(fichario, arquivo);
			} else {
				ProjetoSplit.novaAba(fichario, file);
			}
		}
	}

	private void setComponent(Separador separador, Component c) {
		if (separador != null) {
			if (separador.getLeftComponent() == null) {
				separador.setLeftComponent(c);
			} else {
				separador.setRightComponent(c);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("fichario".equals(qName)) {
			if (fichario.getParent() instanceof Separador) {
				separador = (Separador) fichario.getParent();
			}
			fichario = null;
		} else if ("separador".equals(qName)) {
			if (separador.getParent() instanceof Separador) {
				separador = (Separador) separador.getParent();
			}
			fichario = null;
		}
	}
}