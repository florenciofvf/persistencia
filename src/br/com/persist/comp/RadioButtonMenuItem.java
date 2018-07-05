package br.com.persist.comp;

import javax.swing.Icon;
import javax.swing.JRadioButtonMenuItem;

import br.com.persist.util.Mensagens;

public class RadioButtonMenuItem extends JRadioButtonMenuItem {
	private static final long serialVersionUID = 1L;

	public RadioButtonMenuItem() {
	}

	public RadioButtonMenuItem(String chaveRotulo, Icon icon, boolean selecionado) {
		super(Mensagens.getString(chaveRotulo), icon, selecionado);
	}

	public RadioButtonMenuItem(String chaveRotulo, boolean selecionado) {
		super(Mensagens.getString(chaveRotulo), selecionado);
	}

	public RadioButtonMenuItem(String chaveRotulo, Icon icon) {
		super(Mensagens.getString(chaveRotulo), icon);
	}

	public RadioButtonMenuItem(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}
}