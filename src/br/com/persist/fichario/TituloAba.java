package br.com.persist.fichario;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicButtonUI;

import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;

public class TituloAba extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Icon[] ICONES = { Icones.CUBO, Icones.PANEL2, Icones.TABELA, Icones.EXPANDIR, Icones.PANEL4,
			Icones.CONFIG, Icones.BANCO, Icones.FRAGMENTO, Icones.CRIAR, Icones.UPDATE, Icones.ANEXO, Icones.CAMPOS,
			Icones.REFERENCIA, Icones.VAR, Icones.CENTRALIZAR, Icones.URL };
	private static final Logger LOG = Logger.getGlobal();
	public static final byte OBJETOS = 0;
	public static final byte DESKTOP = 1;
	public static final byte CONSULTA = 2;
	public static final byte ARVORE = 3;
	public static final byte ANOTACAO = 4;
	public static final byte CONFIG = 5;
	public static final byte CONEXAO = 6;
	public static final byte FRAGMENTO = 7;
	public static final byte OBJETO = 8;
	public static final byte UPDATE = 9;
	public static final byte ANEXO = 10;
	public static final byte METADADO = 11;
	public static final byte MAPEAMENTO = 12;
	public static final byte VARIAVEIS = 13;
	public static final byte COMPARACAO = 14;
	public static final byte REQUISICAO = 15;
	private final Fichario fichario;

	public TituloAba(Fichario fichario, byte tipo) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		Objects.requireNonNull(fichario);
		this.fichario = fichario;
		add(new Rotulo(tipo));
		setOpaque(false);
		add(new Icone());
	}

	private class Rotulo extends Label {
		private static final long serialVersionUID = 1L;

		Rotulo(byte tipo) {
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			setIcon(ICONES[tipo]);
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
			setToolTipText(Mensagens.getString(Constantes.LABEL_FECHAR));
			setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(17, 17));
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
			int i = fichario.indexOfTabComponent(TituloAba.this);

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

			int delta = 6;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			g2.dispose();
		}
	}

	public static final transient MouseListener mouseListenerInner = new MouseAdapter() {
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