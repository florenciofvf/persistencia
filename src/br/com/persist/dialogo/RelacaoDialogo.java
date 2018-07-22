package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.PanelCenter;
import br.com.persist.comp.TabbedPane;
import br.com.persist.comp.TextArea;
import br.com.persist.formulario.Superficie;

public class RelacaoDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final Superficie superficie;
	private final Relacao relacao;

	public RelacaoDialogo(Frame frame, Superficie superficie, Relacao relacao) {
		super(frame, relacao.getOrigem().getId() + " / " + relacao.getDestino().getId(), 500, 200, false);
		this.superficie = superficie;
		this.relacao = relacao;
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new Fichario());
	}

	protected void processar() {
	}

	private class PanelGeral extends Panel {
		private static final long serialVersionUID = 1L;

		PanelGeral() {
			super(new GridLayout(1, 2));
			add(new PanelLado(true));
			add(new PanelLado(false));
		}
	}

	private class PanelDesc extends PanelBorder {
		private static final long serialVersionUID = 1L;
		private final TextArea textArea = new TextArea();

		PanelDesc() {
			textArea.setText(relacao.getDescricao());
			textArea.addKeyListener(keyListener);
			add(BorderLayout.CENTER, textArea);
		}

		private KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				relacao.setDescricao(textArea.getText());
			}
		};
	}

	private class PanelCor extends PanelBorder implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final JColorChooser colorChooser;

		PanelCor() {
			colorChooser = new JColorChooser(relacao.getCor());
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			relacao.setCor(colorChooser.getColor());
			superficie.repaint();
		}
	}

	private class PanelLado extends Panel {
		private static final long serialVersionUID = 1L;
		private final CheckBox chkPonto = new CheckBox("label.ponto");
		private final boolean origem;

		PanelLado(boolean origem) {
			super(new GridLayout(3, 1));
			this.origem = origem;
			chkPonto.setSelected(origem ? relacao.isPontoOrigem() : relacao.isPontoDestino());
			chkPonto.addActionListener(pontoListener);

			Label label = new Label();
			label.setText(origem ? relacao.getOrigem().getId() : relacao.getDestino().getId());

			add(new PanelCenter(label));
			add(new PanelCenter(new PanelObjeto(origem ? relacao.getOrigem() : relacao.getDestino())));
			add(new PanelCenter(chkPonto));
		}

		private ActionListener pontoListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (origem) {
					relacao.setPontoOrigem(chkPonto.isSelected());
				} else {
					relacao.setPontoOrigem(chkPonto.isSelected());
				}

				superficie.repaint();
			}
		};
	}

	private class PanelObjeto extends Panel {
		private static final long serialVersionUID = 1L;
		private final Objeto objeto;

		PanelObjeto(Objeto objeto) {
			super(null);
			final int lado = Objeto.diametro + 10;
			this.objeto = new Objeto(5, 5, objeto.getCor(), objeto.getIcone());
			setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(lado, lado));
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