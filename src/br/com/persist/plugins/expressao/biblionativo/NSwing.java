package br.com.persist.plugins.expressao.biblionativo;

import javax.swing.JOptionPane;

public class NSwing {
	private NSwing() {
	}

	@Biblio(1)
	public static void mensagem(Object string) {
		JOptionPane.showMessageDialog(null, string);
	}

	@Biblio(2)
	public static String getString(Object string) {
		String resp = JOptionPane.showInputDialog(null, string);
		if (resp == null) {
			resp = "";
		}
		return resp;
	}
}