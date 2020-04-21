package br.com.persist.comp;

import javax.swing.JTextField;

import br.com.persist.util.Constantes;

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