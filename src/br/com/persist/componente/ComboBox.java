package br.com.persist.componente;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class ComboBox<E> extends JComboBox<E> {
	private static final long serialVersionUID = 3731076329837849730L;

	public ComboBox(ComboBoxModel<E> model) {
		super(model);
	}

	public ComboBox(E[] itens) {
		super(itens);
	}

	public ComboBox() {
		super();
	}
}