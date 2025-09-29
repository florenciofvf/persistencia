package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import javax.swing.JTextPane;

public class ToolbarPesquisa extends BarraButton implements ActionListener {
	private final TextField txtPesquisa = new TextField(35);
	private static final long serialVersionUID = 1L;
	private transient Selecao selecao;
	private final JTextPane textPane;

	public ToolbarPesquisa(JTextPane textPane) {
		super.ini(new Nil(), LIMPAR, COPIAR, COLAR);
		txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
		this.textPane = Objects.requireNonNull(textPane);
		txtPesquisa.addActionListener(this);
		add(txtPesquisa);
		add(label);
	}

	@Override
	public void focusInputPesquisar() {
		txtPesquisa.requestFocus();
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
		if (!Util.isEmpty(txtPesquisa.getText())) {
			selecao = Util.getSelecao(textPane, selecao, txtPesquisa.getText());
			selecao.selecionar(label);
		} else {
			label.limpar();
		}
	}
}