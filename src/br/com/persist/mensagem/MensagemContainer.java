package br.com.persist.mensagem;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Text;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextField;

public class MensagemContainer extends Panel {
	private final TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final File file;

	public MensagemContainer(Janela janela, String string, File file) {
		this.file = file;
		if (Util.isMensagemHtml()) {
			textEditor.setContentType("text/html");
			textEditor.setEditable(false);
			Util.setMensagemHtml(false);
		}
		textEditor.setText(string);
		toolbar.ini(janela);
		montarLayout();
	}

	public MensagemContainer(Janela janela, List<Text> listaText) throws BadLocationException {
		this.file = null;
		StyleContext sc = new StyleContext();
		DefaultStyledDocument doc = new DefaultStyledDocument(sc);
		configStyleContext(sc);
		insertString(listaText, doc, sc);
		textEditor.setStyledDocument(doc);
		toolbar.ini(janela);
		montarLayout();
	}

	private void configStyleContext(StyleContext styleContext) {
		Style def = styleContext.getStyle(StyleContext.DEFAULT_STYLE);

		Style padrao = styleContext.addStyle("padrao", def);
		StyleConstants.setFontFamily(padrao, "SansSerif");
		StyleConstants.setBold(padrao, true);

		Style none = styleContext.addStyle("none", padrao);
		StyleConstants.setForeground(none, Color.BLACK);

		Style info = styleContext.addStyle("info", padrao);
		StyleConstants.setForeground(info, Color.BLUE);

		Style warn = styleContext.addStyle("warn", padrao);
		StyleConstants.setForeground(warn, Color.ORANGE);

		Style erro = styleContext.addStyle("erro", padrao);
		StyleConstants.setForeground(erro, Color.RED);
		StyleConstants.setItalic(erro, true);

		Style nota = styleContext.addStyle("nota", padrao);
		StyleConstants.setForeground(nota, new Color(0, 153, 153));

		Style show = styleContext.addStyle("show", padrao);
		StyleConstants.setForeground(show, Color.MAGENTA);
	}

	private void insertString(List<Text> listaText, DefaultStyledDocument document, StyleContext styleContext)
			throws BadLocationException {
		if (listaText == null) {
			return;
		}
		for (Text text : listaText) {
			Style style = styleContext.getStyle(text.getIdStyle());
			document.insertString(document.getLength(), text.getContent() + Constantes.QL, style);
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		ScrollPane scrollPane = new ScrollPane(textEditor);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
	}

	public void setSel(String string) {
		if (string != null) {
			toolbar.txtPesquisa.setText(string);
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini(Janela janela) {
			if (file != null) {
				super.ini(janela, COPIAR, COLAR, SALVAR);
			} else {
				super.ini(janela, COPIAR, COLAR);
			}
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textEditor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textEditor.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textEditor, numeros, letras);
		}

		@Override
		protected void salvar() {
			try {
				ArquivoUtil.salvar(textEditor, file);
				salvoMensagem();
			} catch (Exception e) {
				Util.mensagem(MensagemContainer.this, e.getMessage());
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textEditor, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}

	public void dialogOpenedHandler() {
		textEditor.requestFocus();
	}
}