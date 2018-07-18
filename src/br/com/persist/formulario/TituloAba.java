package br.com.persist.formulario;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicButtonUI;

import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.util.Mensagens;

public class TituloAba extends Panel {
	private static final long serialVersionUID = 1L;
	private final Fichario fichario;

	public TituloAba(Fichario fichario) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		Objects.requireNonNull(fichario);
		this.fichario = fichario;
		add(new Rotulo());
		setOpaque(false);
		add(new Icone());
	}

	private class Rotulo extends Label {
		private static final long serialVersionUID = 1L;

		Rotulo() {
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		}

		@Override
		public String getText() {
			int i = fichario.indexOfTabComponent(TituloAba.this);

			if (i != -1) {
				return fichario.getTitleAt(i);
			}

			return null;
		}
	}

	private class Icone extends Button implements ActionListener {
		private static final long serialVersionUID = 1L;

		Icone() {
			setToolTipText(Mensagens.getString("label.fechar"));
			setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(17, 17));
			addMouseListener(mouseListener);
			setContentAreaFilled(false);
			setUI(new BasicButtonUI());
			setRolloverEnabled(true);
			setBorderPainted(false);
			addActionListener(this);
			setFocusable(false);
		}

		public void actionPerformed(ActionEvent e) {
			int i = fichario.indexOfTabComponent(TituloAba.this);

			if (i != -1) {
				fichario.remove(i);
			}
		}

		public void updateUI() {
		}

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

			int delta = 6;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			g2.dispose();
		}
	}

	private MouseListener mouseListener = new MouseAdapter() {
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();

			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}

		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();

			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};
}