package br.com.persist.componente;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import br.com.persist.util.Mensagens;

public class CheckBoxItem extends JCheckBoxMenuItem {
	private static final long serialVersionUID = 1L;

	public CheckBoxItem(String chaveRotulo, Icon icon) {
		super(Mensagens.getString(chaveRotulo), icon);
	}

	public CheckBoxItem() {
	}
}