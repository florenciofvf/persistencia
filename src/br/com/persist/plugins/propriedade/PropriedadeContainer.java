package br.com.persist.plugins.propriedade;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginBasico;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TabbedPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextEditorListener;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class PropriedadeContainer extends AbstratoContainer implements PluginBasico {
	private final PainelResultado painelResultado = new PainelResultado();
	private final FicharioInner ficharioInner = new FicharioInner();
	private PropriedadeFormulario propriedadeFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private PropriedadeDialogo propriedadeDialogo;
	private final File fileProper;
	private final File fileDesenv;
	private final File filePrehom;
	private final File fileHomolo;

	public PropriedadeContainer(Janela janela, Formulario formulario) {
		super(formulario);
		fileProper = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR
				+ PropriedadeConstantes.PROPRIEDADES + ".xml");
		fileDesenv = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + "desenvolvimento.txt");
		filePrehom = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + "pre_homologacao.txt");
		fileHomolo = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + "homologacao.txt");
		toolbar.ini(janela);
		montarLayout();
	}

	public PropriedadeDialogo getPropriedadeDialogo() {
		return propriedadeDialogo;
	}

	public void setPropriedadeDialogo(PropriedadeDialogo propriedadeDialogo) {
		this.propriedadeDialogo = propriedadeDialogo;
		if (propriedadeDialogo != null) {
			propriedadeFormulario = null;
		}
	}

	public PropriedadeFormulario getPropriedadeFormulario() {
		return propriedadeFormulario;
	}

	public void setPropriedadeFormulario(PropriedadeFormulario propriedadeFormulario) {
		this.propriedadeFormulario = propriedadeFormulario;
		if (propriedadeFormulario != null) {
			propriedadeDialogo = null;
		}
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanel(), criarPanelResultado());
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.5));
		split.setOneTouchExpandable(true);
		split.setContinuousLayout(true);
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanel() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbar);
		ficharioInner.init();
		panel.add(BorderLayout.CENTER, ficharioInner);
		return panel;
	}

	private transient TextEditorListener listener = TextEditor.newTextEditorAdapter(toolbar::focusInputPesquisar);

	private class FicharioInner extends TabbedPane {
		private static final long serialVersionUID = 1L;

		void init() {
			Aba aba = new Aba(0, fileProper, listener);
			aba.montarLayout();
			addTab("label.configuracoes", Icones.CONFIG, aba);

			aba = new Aba(1, fileDesenv, listener);
			aba.montarLayout();
			addTab("label.desenvolvimento", Icones.CONFIG2, aba);

			aba = new Aba(2, filePrehom, listener);
			aba.montarLayout();
			addTab("label.pre_homologacao", Icones.CONFIG2, aba);

			aba = new Aba(3, fileHomolo, listener);
			aba.montarLayout();
			addTab("label.homologacao", Icones.CONFIG2, aba);
		}
	}

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		panel.add(BorderLayout.CENTER, painelResultado);
		return panel;
	}

	private class PainelResultado extends Panel {
		private TextEditor textEditor = new TextEditor();
		private static final long serialVersionUID = 1L;
		private final ToolbarPesquisa toolbarPesquisa;

		private PainelResultado() {
			toolbarPesquisa = new ToolbarPesquisa(textEditor);
			textEditor.setListener(TextEditor.newTextEditorAdapter(toolbarPesquisa::focusInputPesquisar));
			add(BorderLayout.NORTH, toolbarPesquisa);
			ScrollPane scrollPane = new ScrollPane(textEditor);
			scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
			Panel panelScroll = new Panel();
			panelScroll.add(BorderLayout.CENTER, scrollPane);
			add(BorderLayout.CENTER, new ScrollPane(panelScroll));
			Action copiar2Action = toolbarPesquisa.addCopiar2();
			copiar2Action.hint(PropriedadeMensagens.getString("label.copiar_tudo"));
			copiar2Action.setActionListener(e -> copiar2());
		}

		private void setText(String string) {
			textEditor.setText(string);
			SwingUtilities.invokeLater(() -> textEditor.scrollRectToVisible(new Rectangle()));
		}

		void setFontTextArea(Font font) {
			textEditor.setFont(font);
		}

		private void processar(Arvore raiz) {
			textEditor.setText(Constantes.VAZIO);
			try {
				raiz.gerarProperty(textEditor.getStyledDocument());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		private void copiar2() {
			String string = textEditor.getText();
			Util.setContentTransfered(string);
			toolbarPesquisa.copiar2Mensagem(string);
			textEditor.setSelectionStart(0);
			textEditor.setSelectionEnd(string.length());
			textEditor.requestFocus();
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	protected class Toolbar extends BarraButton implements ActionListener {
		private JComboBox<String> comboFontes = new JComboBox<>(PropriedadeConstantes.FONTES);
		private Action gerarAcao = acaoIcon("label.gerar_conteudo", Icones.EXECUTAR);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, LIMPAR, BAIXAR, SALVAR,
					COPIAR, COLAR);
			addButton(gerarAcao);
			add(comboFontes);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
			comboFontes.addItemListener(Toolbar.this::alterarFonte);
			gerarAcao.setActionListener(e -> gerar());
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(PropriedadeMensagens.getString(chave), icon);
		}

		private void gerar() {
			Aba aba = (Aba) ficharioInner.getSelectedComponent();
			if (aba == null || !aba.isValido() || aba.indice != 0) {
				return;
			}
			String string = aba.getText();
			if (Util.isEmpty(string)) {
				painelResultado.setText("Editor vazio.");
				return;
			}
			try {
				Arvore raiz = PropriedadeUtil.criarRaiz(string);
				if (raiz != null) {
					painelResultado.processar(raiz);
					colorTextArea(aba, raiz);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		private void colorTextArea(Aba aba, Arvore raiz) {
			aba.setText(Constantes.VAZIO);
			try {
				raiz.print(aba.editor.getStyledDocument());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(PropriedadeContainer.this)) {
				PropriedadeFormulario.criar(formulario, PropriedadeContainer.this);
			} else if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
				PropriedadeFormulario.criar(formulario, PropriedadeContainer.this);
			}
		}

		private void alterarFonte(ItemEvent e) {
			if (ItemEvent.SELECTED == e.getStateChange()) {
				Object object = comboFontes.getSelectedItem();
				if (object instanceof String) {
					Font font = getFont();
					alterar(font, (String) object);
				}
			}
		}

		private void alterar(Font font, String nome) {
			if (font != null) {
				Font nova = new Font(nome, font.getStyle(), font.getSize());
				painelResultado.setFontTextArea(nova);
				Aba aba = (Aba) ficharioInner.getSelectedComponent();
				if (aba != null && aba.isValido()) {
					aba.setFont(nova);
				}
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (propriedadeFormulario != null) {
				propriedadeFormulario.excluirContainer();
				formulario.adicionarPagina(PropriedadeContainer.this);
			} else if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
				formulario.adicionarPagina(PropriedadeContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
			}
			PropriedadeFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		protected void limpar() {
			Aba aba = (Aba) ficharioInner.getSelectedComponent();
			if (aba != null && aba.isValido()) {
				aba.setText(Constantes.VAZIO);
			}
			selecao = null;
			label.limpar();
		}

		@Override
		public void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		private void abrir() {
			Aba aba = (Aba) ficharioInner.getSelectedComponent();
			if (aba != null) {
				abrirArquivo(aba);
			}
		}

		private void abrirArquivo(Aba aba) {
			aba.montarLayout();
			if (aba.indice == 0) {
				toolbar.gerar();
			}
		}

		@Override
		public void salvar() {
			if (Util.confirmaSalvar(PropriedadeContainer.this, Constantes.TRES)) {
				Aba aba = (Aba) ficharioInner.getSelectedComponent();
				if (aba != null && aba.isValido()) {
					salvarArquivo(aba);
				}
			}
		}

		private void salvarArquivo(Aba aba) {
			if (aba == null || !aba.isValido()) {
				return;
			}
			try {
				ArquivoUtil.salvar(aba.editor, aba.file);
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		@Override
		protected void copiar() {
			Aba aba = (Aba) ficharioInner.getSelectedComponent();
			if (aba != null && aba.isValido()) {
				String string = Util.getString(aba.editor);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				aba.editor.requestFocus();
			}
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Aba aba = (Aba) ficharioInner.getSelectedComponent();
			if (aba != null && aba.isValido()) {
				Util.getContentTransfered(aba.editor, numeros, letras);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				Aba aba = (Aba) ficharioInner.getSelectedComponent();
				if (aba != null && aba.isValido()) {
					selecao = Util.getSelecao(aba.editor, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return PropriedadeFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE_MIN);
			}

			@Override
			public String getTitulo() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE);
			}

			@Override
			public String getHint() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE);
			}

			@Override
			public Icon getIcone() {
				return Icones.EDIT;
			}
		};
	}
}

class Aba extends Panel {
	private static final long serialVersionUID = 1L;
	transient TextEditorListener listener;
	TextEditor editor;
	final int indice;
	final File file;

	Aba(int indice, File file, TextEditorListener listener) {
		this.listener = listener;
		this.indice = indice;
		this.file = file;
	}

	void montarLayout() {
		removeAll();
		Panel panelArea = new Panel();
		editor = new TextEditor();
		panelArea.add(BorderLayout.CENTER, editor);
		ScrollPane scrollPane = new ScrollPane(panelArea);
		scrollPane.setRowHeaderView(new TextEditorLine(editor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		editor.setText(abrirArquivo());
		editor.setListener(listener);
	}

	private String abrirArquivo() {
		if (file.exists()) {
			try {
				return ArquivoUtil.getString(file);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, this);
				return null;
			}
		}
		return null;
	}

	void setText(String string) {
		if (isValido()) {
			editor.setText(string);
		}
	}

	String getText() {
		if (isValido()) {
			return editor.getText();
		}
		return "";
	}

	void setFonte(Font font) {
		if (isValido()) {
			editor.setFont(font);
		}
	}

	boolean isValido() {
		return editor != null;
	}
}