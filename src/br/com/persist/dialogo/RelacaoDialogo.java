package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.comp.PanelBorder;
import br.com.persist.formulario.Superficie;

public class RelacaoDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkPonto1 = new CheckBox("label.ponto");
	private final CheckBox chkPonto2 = new CheckBox("label.ponto");
	private final Superficie superficie;
	private final Relacao relacao;
	private final Objeto objeto1;
	private final Objeto objeto2;

	public RelacaoDialogo(Frame frame, Superficie superficie, Objeto objeto1, Objeto objeto2) {
		super(frame, objeto1.getId() + " / " + objeto2.getId(), 700, 300, true);
		relacao = superficie.getRelacao(objeto1, objeto2);
		this.superficie = superficie;
		this.objeto1 = objeto1;
		this.objeto2 = objeto2;
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		Panel panel = new Panel(new GridLayout(2, 1));

		panel.add(new PanelLinha(objeto1, relacao != null ? relacao.isPonto1() : false));
		panel.add(new PanelLinha(objeto2, relacao != null ? relacao.isPonto2() : false));

		chkPonto1.setSelected(relacao != null ? relacao.isPonto1() : false);
		chkPonto2.setSelected(relacao != null ? relacao.isPonto2() : false);

		add(BorderLayout.CENTER, panel);
	}

	protected void processar() {
		if (relacao != null) {
			relacao.setPonto1(chkPonto1.isSelected());
			relacao.setPonto2(chkPonto2.isSelected());
		} else {
			Relacao relacao = new Relacao(objeto1, chkPonto1.isSelected(), objeto2, chkPonto2.isSelected());
			superficie.addRelacao(relacao);
		}

		superficie.repaint();
		dispose();
	}

	private class PanelLinha extends PanelBorder {
		private static final long serialVersionUID = 1L;

		PanelLinha(Objeto objeto, boolean ponto) {
			add(BorderLayout.WEST, new PanelObjeto(objeto));
			add(BorderLayout.CENTER, new JLabel(objeto.getId()));
			add(BorderLayout.EAST, chkPonto1);
		}
	}

	private class PanelObjeto extends PanelBorder {
		private static final long serialVersionUID = 1L;
		private final Objeto objeto;

		PanelObjeto(Objeto objeto) {
			this.objeto = new Objeto(5, 5, objeto.getCor(), objeto.getIcone());
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			objeto.desenhar(this, g2);
		}
	}
}