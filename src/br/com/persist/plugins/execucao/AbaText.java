package br.com.persist.plugins.execucao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextField;

public class AbaText extends Panel {
	private final TextArea textArea = new TextArea();
	private static final long serialVersionUID = 1L;
	private final transient Arquivo arquivo;
	final Toolbar toolbar = new Toolbar();
	private JScrollPane scrollPane;

	AbaText(Arquivo arquivo) {
		this.arquivo = arquivo;
		montarLayout();
	}

	void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		scrollPane = new JScrollPane(textArea);
		add(BorderLayout.CENTER, scrollPane);
	}

	int getValueScrollPane() {
		return scrollPane.getVerticalScrollBar().getValue();
	}

	void setValueScrollPane(int value) {
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
	}

	String getConteudo() {
		return textArea.getText();
	}

	void abrir() {
		textArea.setText(Constantes.VAZIO);
		if (arquivo.getFile().exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(arquivo.getFile()), StandardCharsets.UTF_8))) {
				StringBuilder sb = new StringBuilder();
				int value = getValueScrollPane();
				String linha = br.readLine();
				while (linha != null) {
					sb.append(linha + Constantes.QL);
					linha = br.readLine();
				}
				textArea.setText(sb.toString());
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
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		void ini() {
			super.ini(new Nil(), LIMPAR, BAIXAR, SALVAR, COPIAR, COLAR);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
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
			textArea.setText(Constantes.VAZIO);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
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
			if (Util.confirmaSalvar(this, Constantes.TRES)) {
				salvarArquivo(arquivo.getFile());
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, this);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}
}