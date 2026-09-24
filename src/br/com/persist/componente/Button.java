package br.com.persist.componente;

import javax.swing.Action;
import javax.swing.JButton;

import br.com.persist.assistencia.Icone;
import br.com.persist.assistencia.Mensagens;

public class Button extends JButton {
	private static final long serialVersionUID = 1L;
	private String tag;

	public Button(String rotulo, boolean chaveRotulo) {
		super(chaveRotulo ? Mensagens.getString(rotulo) : rotulo);
	}

	public Button(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}

	public Button(Action action) {
		super(action);
	}

	public Button() {
	}

	public void setIcone(Icone icone) {
		setIcon(icone != null ? icone.getIcon() : null);
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}