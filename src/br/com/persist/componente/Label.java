package br.com.persist.componente;

import java.awt.Color;

import javax.swing.JLabel;

import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Label extends JLabel {
	private static final long serialVersionUID = 1L;
	private LabelLinkListener linkListener;

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

	public void modoLink() {
		setForeground(Color.BLUE);
		setOpaque(true);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (linkListener != null) {
					linkListener.click(Label.this);
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

	public LabelLinkListener getLinkListener() {
		return linkListener;
	}

	public void setLinkListener(LabelLinkListener linkListener) {
		this.linkListener = linkListener;
	}
}