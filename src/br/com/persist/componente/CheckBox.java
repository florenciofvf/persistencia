package br.com.persist.componente;

import javax.swing.JCheckBox;

import br.com.persist.assistencia.Mensagens;

public class CheckBox extends JCheckBox {
	private static final long serialVersionUID = 1L;

	public CheckBox(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}

	public CheckBox() {
	}
}