package br.com.persist.comp;

import javax.swing.JCheckBoxMenuItem;

import br.com.persist.util.Mensagens;

public class ItemCheckBox extends JCheckBoxMenuItem {
	private static final long serialVersionUID = 1L;

	public ItemCheckBox(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}

	public ItemCheckBox() {
	}
}