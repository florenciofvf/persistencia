package br.com.persist.fichario;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicButtonUI;

import br.com.persist.comp.Button;
import br.com.persist.comp.Panel;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class TituloAbaS extends Panel {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Fichario fichario;

	public TituloAbaS(Fichario fichario) {
		Objects.requireNonNull(fichario);
		this.fichario = fichario;
		add(new Icone());
	}

	private class Icone extends Button implements ActionListener {
		private static final long serialVersionUID = 1L;

		Icone() {
			setToolTipText(Mensagens.getString(Constantes.LABEL_FECHAR));
			setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(6, 6));
			addMouseListener(mouseListenerInner);
			setContentAreaFilled(false);
			setUI(new BasicButtonUI());
			setRolloverEnabled(true);
			setBorderPainted(false);
			addActionListener(this);
			setFocusable(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int i = fichario.indexOfTabComponent(TituloAbaS.this);

			if (i != -1) {
				fichario.remove(i);
			}
		}

		@Override
		public void updateUI() {
			LOG.log(Level.FINEST, "updateUI");
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();

			if (getModel().isPressed()) {
				g2.translate(1, 1);
			}

			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.BLACK);

			if (getModel().isRollover()) {
				g2.setColor(Color.MAGENTA);
			}

			int delta = 3;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			g2.dispose();
		}
	}

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();

			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();

			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};
}