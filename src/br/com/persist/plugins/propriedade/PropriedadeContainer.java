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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
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
import br.com.persist.componente.TextField;
import br.com.persist.componente.TextPane;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class PropriedadeContainer extends AbstratoContainer {
	private final PainelResultado painelResultado = new PainelResultado();
	private final FicharioInner ficharioInner = new FicharioInner();
	private final TextEditor textEditorDesenv = new TextEditor();
	private final TextEditor textEditorPrehom = new TextEditor();
	private final TextEditor textEditorHomolo = new TextEditor();
	private final TextEditor textEditor = new TextEditor();
	private PropriedadeFormulario propriedadeFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private PropriedadeDialogo propriedadeDialogo;
	private final File fileDesenv;
	private final File filePrehom;
	private final File fileHomolo;
	private final File file;

	public PropriedadeContainer(Janela janela, Formulario formulario) {
		super(formulario);
		file = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + PropriedadeConstantes.PROPRIEDADES
				+ ".xml");
		fileDesenv = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + "desenvolvimento.txt");
		filePrehom = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + "pre_homologacao.txt");
		fileHomolo = new File(PropriedadeConstantes.PROPRIEDADES + Constantes.SEPARADOR + "homologacao.txt");
		toolbar.ini(janela);
		montarLayout();
		abrir();
		abrirArquivo(textEditorDesenv, fileDesenv);
		abrirArquivo(textEditorPrehom, filePrehom);
		abrirArquivo(textEditorHomolo, fileHomolo);
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

	private class FicharioInner extends TabbedPane {
		private static final long serialVersionUID = 1L;

		void init() {
			addTab("label.configuracoes", Icones.CONFIG, new Aba(textEditor, file));
			addTab("label.desenvolvimento", Icones.CONFIG2, new Aba(textEditorDesenv, fileDesenv));
			addTab("label.pre_homologacao", Icones.CONFIG2, new Aba(textEditorPrehom, filePrehom));
			addTab("label.homologacao", Icones.CONFIG2, new Aba(textEditorHomolo, fileHomolo));
		}
	}

	private class Aba extends Panel {
		private static final long serialVersionUID = 1L;
		final TextPane textPane;
		final File file;

		Aba(TextPane textPane, File file) {
			this.textPane = textPane;
			this.file = file;
			Panel panelArea = new Panel();
			panelArea.add(BorderLayout.CENTER, textPane);
			ScrollPane scrollPane = new ScrollPane(panelArea);
			scrollPane.setRowHeaderView(new TextEditorLine(textPane));
			Panel panelScroll = new Panel();
			panelScroll.add(BorderLayout.CENTER, scrollPane);
			add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		}
	}

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		panel.add(BorderLayout.CENTER, painelResultado);
		return panel;
	}

	private class PainelResultado extends Panel {
		private static final long serialVersionUID = 1L;
		private TextEditor textPane = new TextEditor();
		private final ToolbarPesquisa toolbarPesquisa;

		private PainelResultado() {
			toolbarPesquisa = new ToolbarPesquisa(textPane);
			add(BorderLayout.NORTH, toolbarPesquisa);
			ScrollPane scrollPane = new ScrollPane(textPane);
			scrollPane.setRowHeaderView(new TextEditorLine(textPane));
			Panel panelScroll = new Panel();
			panelScroll.add(BorderLayout.CENTER, scrollPane);
			add(BorderLayout.CENTER, new ScrollPane(panelScroll));
			Action copiar2Action = toolbarPesquisa.addCopiar2();
			copiar2Action.hint(PropriedadeMensagens.getString("label.copiar_tudo"));
			copiar2Action.setActionListener(e -> copiar2());
		}

		private void setText(String string) {
			textPane.setText(string);
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));
		}

		void setFontTextArea(Font font) {
			textPane.setFont(font);
		}

		private void processar(Arvore raiz) {
			textPane.setText(Constantes.VAZIO);
			try {
				raiz.gerarProperty(textPane.getStyledDocument());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		private void copiar2() {
			String string = textPane.getText();
			Util.setContentTransfered(string);
			toolbarPesquisa.copiar2Mensagem(string);
			textPane.setSelectionStart(0);
			textPane.setSelectionEnd(string.length());
			textPane.requestFocus();
		}
	}

	private void abrir() {
		Aba aba = (Aba) ficharioInner.getSelectedComponent();
		if (aba == null || aba.file == file) {
			abrirArquivo(file);
		} else {
			abrirArquivo(aba);
		}
	}

	private void abrirArquivo(File file) {
		abrirArquivo(textEditor, file);
		toolbar.gerar();
	}

	private void abrirArquivo(Aba aba) {
		abrirArquivo(aba.textPane, aba.file);
	}

	private void abrirArquivo(TextPane textPane, File file) {
		textPane.limpar();
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					textPane.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	protected class Toolbar extends BarraButton implements ActionListener {
		private JComboBox<String> comboFontes = new JComboBox<>(PropriedadeConstantes.FONTES);
		private Action gerarAcao = acaoIcon("label.gerar_conteudo", Icones.EXECUTAR);
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, LIMPAR, BAIXAR, SALVAR,
					COPIAR, COLAR);
			addButton(gerarAcao);
			add(comboFontes);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
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
			String string = textEditor.getText();
			if (Util.isEmpty(string)) {
				painelResultado.setText("Editor vazio.");
				return;
			}
			try {
				Arvore raiz = PropriedadeUtil.criarRaiz(string);
				if (raiz != null) {
					painelResultado.processar(raiz);
					colorTextArea(raiz);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		private void colorTextArea(Arvore raiz) {
			textEditor.setText(Constantes.VAZIO);
			try {
				raiz.print(textEditor.getStyledDocument());
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
				if (aba == null || aba.file == file) {
					textEditor.setFont(nova);
				} else {
					aba.textPane.setFont(nova);
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
			if (aba == null || aba.file == file) {
				textEditor.setText(Constantes.VAZIO);
			} else {
				aba.textPane.setText(Constantes.VAZIO);
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

		@Override
		public void salvar() {
			if (Util.confirmaSalvar(PropriedadeContainer.this, Constantes.TRES)) {
				Aba aba = (Aba) ficharioInner.getSelectedComponent();
				if (aba == null || aba.file == file) {
					salvarArquivo(file);
				} else {
					salvarArquivo(aba);
				}
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textEditor.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		private void salvarArquivo(Aba aba) {
			try (PrintWriter pw = new PrintWriter(aba.file, StandardCharsets.UTF_8.name())) {
				pw.print(aba.textPane.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PropriedadeConstantes.PAINEL_PROPRIEDADE, ex, PropriedadeContainer.this);
			}
		}

		@Override
		protected void copiar() {
			Aba aba = (Aba) ficharioInner.getSelectedComponent();
			if (aba == null || aba.file == file) {
				String string = Util.getString(textEditor);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				textEditor.requestFocus();
			} else {
				String string = Util.getString(aba.textPane);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				aba.textPane.requestFocus();
			}
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Aba aba = (Aba) ficharioInner.getSelectedComponent();
			if (aba == null || aba.file == file) {
				Util.getContentTransfered(textEditor, numeros, letras);
			} else {
				Util.getContentTransfered(aba.textPane, numeros, letras);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				Aba aba = (Aba) ficharioInner.getSelectedComponent();
				if (aba == null || aba.file == file) {
					selecao = Util.getSelecao(textEditor, selecao, txtPesquisa.getText());
				} else {
					selecao = Util.getSelecao(aba.textPane, selecao, txtPesquisa.getText());
				}
				selecao.selecionar(label);
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