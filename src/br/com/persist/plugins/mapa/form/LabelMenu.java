package br.com.persist.plugins.mapa.form;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import br.com.persist.plugins.mapa.Objeto;

public class LabelMenu extends JLabel {
	private static final long serialVersionUID = 1L;
	private static final String ESPACO = "     ";

	public LabelMenu(String rotulo, final Objeto objeto, final PainelRaiz painelRaiz) {
		super(ESPACO + rotulo + ESPACO);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				setForeground(Color.BLACK);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setForeground(Color.BLUE);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				painelRaiz.montar(objeto);
			}
		});
	}
}