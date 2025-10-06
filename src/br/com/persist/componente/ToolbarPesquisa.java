package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JTextPane;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;

public class ToolbarPesquisa extends BarraButton implements ActionListener {
	private static final long serialVersionUID = 1L;
	private transient Selecao selecao;
	private final JTextPane textPane;

	public ToolbarPesquisa(JTextPane textPane) {
		super.ini(new Nil(), LIMPAR, COPIAR, COLAR);
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