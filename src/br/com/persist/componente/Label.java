package br.com.persist.componente;

import java.awt.Color;

import javax.swing.JLabel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Label extends JLabel {
	private static final long serialVersionUID = 1L;
	private transient LabelLinkListener linkListener;

	public Label(String rotulo, boolean chaveRotulo) {
		super(chaveRotulo ? Mensagens.getString(rotulo) : rotulo);
	}

	public Label(String chaveRotulo, Color corFonte) {
		super(Mensagens.getString(chaveRotulo));
		setForeground(corFonte);
	}

	public Label(String chaveRotulo) {
		this(chaveRotulo, true);
	}

	public Label(Color corFonte) {
		setForeground(corFonte);
	}

	public Label() {
	}

	public void limpar() {
		setText(Constantes.VAZIO);
	}

	public void modoLink(LabelLinkListener linkListener) {
		this.linkListener = linkListener;
		setForeground(Color.BLUE);
		setOpaque(true);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Label.this.linkListener != null) {
					Label.this.linkListener.click(Label.this);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setForeground(Color.MAGENTA);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setForeground(Color.BLUE);
			}
		});
	}
}