package br.com.persist.plugins.instrucao.biblionativo;

import javax.swing.JOptionPane;

public class ISwing {
	private ISwing() {
	}

	@Biblio
	public static void mensagem(Object string) {
		JOptionPane.showMessageDialog(null, string);
	}
}