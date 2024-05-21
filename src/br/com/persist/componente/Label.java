package br.com.persist.componente;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;

public class Label extends JLabel {
	private transient LabelLinkListener linkListener;
	private static final long serialVersionUID = 1L;
	private Popup popup;

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
		Util.beep();
		setText(Constantes.VAZIO);
	}

	public void modoCopiar() {
		if (popup == null) {
			popup = new Popup();
			popup.add(criarActionCopiar());
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						showPopup();
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) {
						showPopup();
					}
				}

				private void showPopup() {
					if (popup != null) {
						popup.show(Label.this, 5, 5);
					}
				}
			});
		}
	}

	private Action criarActionCopiar() {
		Action action = Action.actionMenuCopiar();
		action.setActionListener(e -> Util.setContentTransfered(getText()));
		return action;
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