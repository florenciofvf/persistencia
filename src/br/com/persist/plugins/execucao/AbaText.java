package br.com.persist.plugins.execucao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;

public class AbaText extends Panel {
	private static final long serialVersionUID = 1L;
	private final Editor editor = new Editor();
	private final transient Arquivo arquivo;
	final Toolbar toolbar = new Toolbar();
	private JScrollPane scrollPane;

	AbaText(Arquivo arquivo) {
		this.arquivo = arquivo;
		montarLayout();
	}

	void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		scrollPane = new JScrollPane(editor);
		scrollPane.setRowHeaderView(new TextEditorLine(editor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		editor.setListener(TextEditor.newTextEditorAdapter(toolbar::focusInputPesquisar, toolbar::salvar));
	}

	int getValueScrollPane() {
		return scrollPane.getVerticalScrollBar().getValue();
	}

	void setValueScrollPane(int value) {
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
	}

	String getConteudo() {
		return editor.getText();
	}

	void abrir() {
		editor.setText(Constantes.VAZIO);
		if (arquivo.getFile().exists()) {
			try {
				int value = getValueScrollPane();
				editor.setText(ArquivoUtil.getString(arquivo.getFile()));
				setValueScrollPane(value);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(ExecucaoConstantes.PAINEL_EXECUCAO, ex, this);
			}
		}
	}

	void ini(String string) {
		toolbar.ini(string);
	}

	void ini() {
		toolbar.ini();
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		void ini() {
			super.ini(new Nil(), LIMPAR, BAIXAR, SALVAR, COPIAR, COLAR);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		void ini(String arqAbsoluto) {
			label.setText(arqAbsoluto);
			add(label);
		}

		@Override
		protected void limpar() {
			editor.setText(Constantes.VAZIO);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
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
			if (Util.confirmaSalvar(this, Constantes.TRES)) {
				salvarArquivo(arquivo.getFile());
			}
		}

		private void salvarArquivo(File file) {
			try {
				ArquivoUtil.salvar(editor, file);
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, this);
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