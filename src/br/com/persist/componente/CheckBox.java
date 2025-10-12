package br.com.persist.componente;

import javax.swing.JCheckBox;

import br.com.persist.assistencia.Mensagens;

public class CheckBox extends JCheckBox {
	private static final long serialVersionUID = 1L;
	private String tag;

	public CheckBox(String rotulo, boolean chaveRotulo) {
		super(chaveRotulo ? Mensagens.getString(rotulo) : rotulo);
	}

	public CheckBox(String chaveRotulo) {
		this(chaveRotulo, true);
	}

	public CheckBox(boolean selecionado) {
		super("", selecionado);
	}

	public CheckBox() {
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}