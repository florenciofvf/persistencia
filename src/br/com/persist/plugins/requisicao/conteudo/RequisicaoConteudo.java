package br.com.persist.plugins.requisicao.conteudo;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Label;
import br.com.persist.componente.TextField;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoException;
import br.com.persist.plugins.requisicao.RequisicaoRota;

public interface RequisicaoConteudo {
	public Component exibir(InputStream is, Tipo parametros, String uri)
			throws RequisicaoException, IOException, BadLocationException;

	public String titulo();

	public Icon icone();

	public void setRequisicaoConteudoListener(RequisicaoConteudoListener listener);

	public RequisicaoConteudoListener getRequisicaoConteudoListener();

	public void setRequisicaoRota(RequisicaoRota rota);

	public RequisicaoRota getRequisicaoRota();

	public default BarraButton criarToolbarPesquisa(JTextPane textPane, String uri) {
		return new ToolbarPesquisa(textPane, uri);
	}

	public default BarraButton criarToolbarPesquisa(String uri) {
		return new ToolbarPesquisa(uri);
	}

	public class ToolbarPesquisa extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextField txtPesquisa = new TextField(35);
		private Label labelUri = new Label();
		private transient Selecao selecao;
		private final JTextPane textPane;

		public ToolbarPesquisa(JTextPane textPane, String uri) {
			super.ini(null, LIMPAR, COPIAR, COLAR);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			labelUri.setText("[" + uri + "]");
			this.textPane = textPane;
			add(txtPesquisa);
			add(label);
			add(labelUri);
		}

		public ToolbarPesquisa(String uri) {
			super.ini(null);
			labelUri.setText("[" + uri + "]");
			this.textPane = null;
			add(labelUri);
		}

		@Override
		protected void limpar() {
			textPane.setText(Constantes.VAZIO);
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textPane);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textPane.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textPane, numeros, letras);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textPane, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}
}