package br.com.persist.mensagem;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;

import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.componente.TextPane;

public class MensagemContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextPane textArea = new TextPane();
	private final Toolbar toolbar = new Toolbar();
	private final File file;

	public MensagemContainer(Janela janela, String string, File file) {
		this.file = file;
		textArea.setText(string);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(textArea));
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextField txtPesquisa = new TextField(35);
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
			String string = Util.getString(textArea);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea, numeros, letras);
		}

		@Override
		protected void salvar() {
			try (PrintWriter pw = new PrintWriter(file)) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception e) {
				Util.mensagem(MensagemContainer.this, e.getMessage());
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
				selecao.selecionar();
				label.setText(selecao.getIndice() + "/" + selecao.getTotal());
			}
		}
	}
}