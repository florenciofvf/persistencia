package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.TabbedPane;
import br.com.persist.comp.TextArea;
import br.com.persist.formulario.Superficie;

public class RelacaoDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkPonto1 = new CheckBox("label.ponto");
	private final CheckBox chkPonto2 = new CheckBox("label.ponto");
	private final TextArea textArea = new TextArea();
	private final Superficie superficie;
	private final Relacao relacao;
	private final Objeto objeto1;
	private final Objeto objeto2;
	private Color cor;

	public RelacaoDialogo(Frame frame, Superficie superficie, Objeto objeto1, Objeto objeto2) {
		super(frame, objeto1.getId() + " / " + objeto2.getId(), 500, 200, true);
		relacao = superficie.getRelacao(objeto1, objeto2);
		cor = relacao != null ? relacao.getCor() : Relacao.COR_PADRAO;
		this.superficie = superficie;
		this.objeto1 = objeto1;
		this.objeto2 = objeto2;
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new Fichario());
	}

	protected void processar() {
		if (relacao != null) {
			relacao.setPonto1(chkPonto1.isSelected());
			relacao.setPonto2(chkPonto2.isSelected());
			relacao.setDescricao(textArea.getText());
			relacao.setCor(cor);
		} else {
			Relacao relacao = new Relacao(objeto1, chkPonto1.isSelected(), objeto2, chkPonto2.isSelected());
			relacao.setDescricao(textArea.getText());
			relacao.setCor(cor);
			superficie.addRelacao(relacao);
		}

		superficie.repaint();
		dispose();
	}

	private class PanelGeral extends PanelBorder {
		private static final long serialVersionUID = 1L;

		PanelGeral() {
			chkPonto1.setSelected(relacao != null ? relacao.isPonto1() : false);
			chkPonto2.setSelected(relacao != null ? relacao.isPonto2() : false);

			Panel panel = new Panel(new GridLayout(2, 1));
			panel.add(new PanelLinha(objeto1, chkPonto1));
			panel.add(new PanelLinha(objeto2, chkPonto2));

			add(BorderLayout.CENTER, panel);
		}
	}

	private class PanelDesc extends PanelBorder {
		private static final long serialVersionUID = 1L;

		PanelDesc() {
			if (relacao != null) {
				textArea.setText(relacao.getDescricao());
			}
			add(BorderLayout.CENTER, textArea);
		}
	}

	private class PanelCor extends PanelBorder implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private JColorChooser colorChooser;

		PanelCor() {
			colorChooser = new JColorChooser(cor);
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			cor = colorChooser.getColor();
		}
	}

	private class PanelLinha extends PanelBorder {
		private static final long serialVersionUID = 1L;

		PanelLinha(Objeto objeto, CheckBox checkBox) {
			add(BorderLayout.WEST, new PanelObjeto(objeto));
			add(BorderLayout.CENTER, new JLabel(objeto.getId()));
			add(BorderLayout.EAST, checkBox);
		}
	}

	private class PanelObjeto extends PanelBorder {
		private static final long serialVersionUID = 1L;
		private final Objeto objeto;

		PanelObjeto(Objeto objeto) {
			this.objeto = new Objeto(5, 5, objeto.getCor(), objeto.getIcone());
			setPreferredSize(new Dimension(Objeto.diametro + 10, Objeto.diametro + 10));
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			objeto.desenhar(this, g2);
		}
	}

	private class Fichario extends TabbedPane {
		private static final long serialVersionUID = 1L;

		Fichario() {
			addTab("label.geral", new PanelGeral());
			addTab("label.desc", new PanelDesc());
			addTab("label.cor", new PanelCor());
		}
	}
}