package br.com.persist.comp;

import java.awt.Color;

import javax.swing.JLabel;

import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class Label extends JLabel {
	private static final long serialVersionUID = 1L;

	public Label(String chaveRotulo, Color corFonte) {
		super(Mensagens.getString(chaveRotulo));
		setForeground(corFonte);
	}

	public Label(String chaveRotulo) {
		super(Mensagens.getString(chaveRotulo));
	}

	public Label(Color corFonte) {
		setForeground(corFonte);
	}

	public Label() {
	}

	public void limpar() {
		setText(Constantes.VAZIO);
	}
}