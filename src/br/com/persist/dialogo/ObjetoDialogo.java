package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.Objeto;
import br.com.persist.comp.Label;
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.PanelCenter;
import br.com.persist.comp.TabbedPane;
import br.com.persist.comp.TextField;
import br.com.persist.formulario.Superficie;
import br.com.persist.util.Util;

public class ObjetoDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final Superficie superficie;
	private final Objeto objeto;

	public ObjetoDialogo(Frame frame, Superficie superficie, Objeto objeto) {
		super(frame, objeto.getId(), 700, 350, false);
		this.superficie = superficie;
		this.objeto = objeto;
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new Fichario());
	}

	protected void processar() {
	}

	private class PanelCor extends PanelBorder implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private JColorChooser colorChooser;

		PanelCor() {
			colorChooser = new JColorChooser(objeto.getCor());
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			objeto.setCor(colorChooser.getColor());
			superficie.repaint();
		}
	}

	private class PanelGeral extends PanelBorder implements ActionListener {
		private static final long serialVersionUID = 1L;
		private TextField txtId = new TextField();
		private TextField txtX = new TextField();
		private TextField txtY = new TextField();
		private Label labelIcone = new Label();

		PanelGeral() {
			txtX.setText("" + objeto.x);
			txtY.setText("" + objeto.y);
			txtId.setText(objeto.getId());

			txtId.addActionListener(this);
			txtX.addActionListener(this);
			txtY.addActionListener(this);

			if (objeto.getIcon() != null) {
				labelIcone.setIcon(objeto.getIcon());
			}

			PanelCenter panelIcone = new PanelCenter(labelIcone);
			panelIcone.setBorder(BorderFactory.createEtchedBorder());
			panelIcone.addMouseListener(new IconeListener(objeto, labelIcone));

			Box container = Box.createVerticalBox();
			container.add(panelIcone);
			container.add(criarLinha("label.id", txtId));
			container.add(criarLinha("label.x", txtX));
			container.add(criarLinha("label.y", txtY));

			add(BorderLayout.CENTER, container);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (txtX == e.getSource()) {
				TextField txt = (TextField) e.getSource();
				objeto.x = getInt(txt.getText(), objeto.x);
				superficie.repaint();

			} else if (txtY == e.getSource()) {
				TextField txt = (TextField) e.getSource();
				objeto.y = getInt(txt.getText(), objeto.y);
				superficie.repaint();

			} else if (txtId == e.getSource()) {
				TextField txt = (TextField) e.getSource();
				String id = txt.getText();

				if (!Util.estaVazio(id)) {
					Objeto obj = new Objeto();
					obj.setId(id);

					if (!superficie.contem(obj)) {
						objeto.setId(id);
						superficie.repaint();
					}
				}
			}
		}
	}

	private int getInt(String s, int padrao) {
		if (Util.estaVazio(s)) {
			return padrao;
		}

		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return padrao;
		}
	}

	private class IconeListener extends MouseAdapter {
		private final Objeto objeto;
		private final Label label;

		public IconeListener(Objeto objeto, Label label) {
			this.objeto = objeto;
			this.label = label;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			new IconeDialogo(ObjetoDialogo.this, objeto, label);
			superficie.repaint();
		}
	};

	private Box criarLinha(String chaveRotulo, JComponent componente) {
		Box box = Box.createHorizontalBox();

		Label label = new Label(chaveRotulo);
		label.setHorizontalAlignment(Label.RIGHT);
		label.setPreferredSize(new Dimension(70, 0));
		label.setMinimumSize(new Dimension(70, 0));

		box.add(label);
		box.add(componente);

		return box;
	}

	private class Fichario extends TabbedPane {
		private static final long serialVersionUID = 1L;

		Fichario() {
			addTab("label.geral", new PanelGeral());
			addTab("label.cor", new PanelCor());
		}
	}
}