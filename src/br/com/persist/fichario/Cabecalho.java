package br.com.persist.fichario;

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

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.componente.Action;
import br.com.persist.componente.Button;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.Popup;

class Cabecalho extends Panel {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final transient Pagina pagina;
	private final Fichario fichario;

	Cabecalho(Fichario fichario, Pagina pagina) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		this.fichario = Objects.requireNonNull(fichario);
		this.pagina = Objects.requireNonNull(pagina);
		setOpaque(false);
		Titulo titulo = pagina.getTitulo();
		String title = Preferencias.isTituloAbaMin() ? titulo.getTituloMin() : titulo.getTitulo();
		if (title != null) {
			add(new IconeLabel(titulo.getIcone()));
		}
		add(new IconeFechar(title != null));
	}

	public Pagina getPagina() {
		return pagina;
	}

	private class IconeLabel extends Label {
		private static final long serialVersionUID = 1L;

		private IconeLabel(Icon icone) {
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			setIcon(icone);
		}

		@Override
		public String getText() {
			int i = fichario.indexOfTabComponent(Cabecalho.this);
			if (i != -1) {
				return fichario.getTitleAt(i);
			}
			return null;
		}
	}

	private class IconeFechar extends Button implements ActionListener {
		private final FecharPopup popup = new FecharPopup();
		private static final long serialVersionUID = 1L;
		private final boolean desenhar;

		private IconeFechar(boolean desenhar) {
			setToolTipText(Mensagens.getString(Constantes.LABEL_FECHAR));
			setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(17, 17));
			addMouseListener(mouseListenerInner);
			setContentAreaFilled(false);
			setUI(new BasicButtonUI());
			this.desenhar = desenhar;
			setRolloverEnabled(true);
			setBorderPainted(false);
			addActionListener(this);
			setFocusable(false);
		}

		private final transient MouseListener mouseListenerInner = new MouseAdapter() {
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

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(IconeFechar.this, e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(IconeFechar.this, e.getX(), e.getY());
				}
			}
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			int i = fichario.indexOfTabComponent(Cabecalho.this);
			if (i != -1) {
				fichario.excluirPagina(i);
			}
		}

		@Override
		public void updateUI() {
			LOG.log(Level.FINEST, "updateUI");
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (desenhar) {
				Graphics2D g2 = (Graphics2D) g.create();
				if (getModel().isPressed()) {
					g2.translate(1, 1);
				}
				g2.setStroke(Constantes.STROKE_PADRAO);
				g2.setColor(Color.BLACK);
				if (getModel().isRollover()) {
					g2.setColor(Color.MAGENTA);
				}
				int delta = 6;
				g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
				g2.drawLine(delta, getHeight() - delta - 1, getWidth() - delta - 1, delta);
				g2.dispose();
			}
		}

		private class FecharPopup extends Popup {
			private Action fecharAcao = actionMenu(Constantes.LABEL_FECHAR, Icones.SAIR);
			private static final long serialVersionUID = 1L;

			private FecharPopup() {
				add(fecharAcao);
				fecharAcao.setActionListener(e -> fechar());
			}

			private void fechar() {
				actionPerformed(null);
			}
		}
	}
}