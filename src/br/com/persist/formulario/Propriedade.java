package br.com.persist.formulario;

import javax.swing.JInternalFrame;

public class Propriedade extends JInternalFrame {
	private static final long serialVersionUID = 1L;

	public Propriedade() {
		super("Teste", true, true, true, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 500);
		setVisible(true);
	}
}