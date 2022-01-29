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
import javax.swing.text.BadLocationException;

import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.TextField;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoException;

public interface RequisicaoConteudo {
	public Component exibir(InputStream is, Tipo parametros)
			throws RequisicaoException, IOException, BadLocationException;

	public String titulo();

	public Icon icone();

	public class ToolbarResultado extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextField txtPesquisa = new TextField(35);
		private transient Selecao selecao;

		private ToolbarResultado() {
			super.ini(null, LIMPAR, COPIAR, COLAR);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		@Override
		protected void limpar() {
			// areaResultados.setText(Constantes.VAZIO);
		}

		@Override
		protected void copiar() {
			// String string = Util.getString(areaResultados);
			// Util.setContentTransfered(string);
			// copiarMensagem(string);
			// areaResultados.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			// Util.getContentTransfered(areaResultados, numeros, letras);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// if (!Util.estaVazio(txtPesquisa.getText())) {
			// selecionarAbaJSON();
			// selecao = Util.getSelecao(areaResultados, selecao,
			// txtPesquisa.getText());
			// selecao.selecionar(label);
			// } else {
			// label.limpar();
			// }
		}
	}

}