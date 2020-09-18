package br.com.persist.componente;

import javax.swing.JTextField;

import br.com.persist.assistencia.Constantes;

public class TextField extends JTextField {
	private static final long serialVersionUID = 1L;

	public TextField(int columns) {
		super(columns);
	}

	public TextField(String text) {
		super(text);
	}

	public TextField() {
	}

	public void limpar() {
		setText(Constantes.VAZIO);
	}
}