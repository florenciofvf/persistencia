package br.com.persist.plugins.labirinto;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.SwingUtilitario;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;

public class AbaViewXML extends Panel {
	private static final long serialVersionUID = 1L;
	final TextEditor textEditor = new TextEditor();
	private final Toolbar toolbar = new Toolbar();
	private final LabirintoFichario fichario;
	private ScrollPane scrollPane;
	private final File file;

	AbaViewXML(LabirintoFichario fichario, File file) {
		this.fichario = fichario;
		this.file = file;
		montarLayout();
	}

	void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, textEditor);
		scrollPane = new ScrollPane(panelArea);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		textEditor.setListener(
				TextEditor.newTextEditorAdapter(toolbar::focusInputPesquisar, fichario::salvar, toolbar::baixar));
	}

	int getValueScrollPane() {
		return scrollPane.getVerticalScrollBar().getValue();
	}

	void setValueScrollPane(int value) {
		SwingUtilitario.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
	}

	String getConteudo() {
		return textEditor.getText();
	}

	void abrir() {
		textEditor.setText(Constantes.VAZIO);
		if (file.exists()) {
			try {
				int value = getValueScrollPane();
				textEditor.setText(ArquivoUtil.getString(file));
				setValueScrollPane(value);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(LabirintoConstantes.PAINEL_LABIRINTO, ex, this);
			}
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		private Toolbar() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		@Override
		protected void limpar() {
			textEditor.setText(Constantes.VAZIO);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
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
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textEditor, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}
}