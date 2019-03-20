package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JSeparator;
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
import br.com.persist.comp.TextField;
import br.com.persist.formulario.Formulario;
import br.com.persist.formulario.Superficie;
import br.com.persist.util.Util;

public class RelacaoDialogo extends DialogoAbstrato {
	private static final long serialVersionUID = 1L;
	private final Superficie superficie;
	private final Relacao relacao;

	public RelacaoDialogo(Frame frame, Superficie superficie, Relacao relacao) {
		super(frame, relacao.getOrigem().getId() + " / " + relacao.getDestino().getId(), false);
		this.superficie = superficie;
		Formulario.macro.limpar();
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

	private Box criarLinha(String chaveRotulo, JComponent componente) {
		Box box = Box.createHorizontalBox();

		Label label = new Label(chaveRotulo);
		label.setHorizontalAlignment(Label.RIGHT);
		label.setPreferredSize(new Dimension(160, 0));
		label.setMinimumSize(new Dimension(160, 0));

		box.add(label);

		if (componente instanceof CheckBox) {
			box.add(componente);
			box.add(Box.createHorizontalGlue());

		} else {
			box.add(componente);
		}

		return box;
	}

	private class PanelDesc extends PanelBorder implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextArea textArea = new TextArea();
		private TextField txtDeslocXDesc = new TextField();
		private TextField txtDeslocYDesc = new TextField();
		private CheckBox chkDesenharDesc = new CheckBox();

		PanelDesc() {
			txtDeslocXDesc.setText("" + relacao.deslocamentoXDesc);
			txtDeslocYDesc.setText("" + relacao.deslocamentoYDesc);
			chkDesenharDesc.setSelected(relacao.isDesenharDescricao());

			txtDeslocXDesc.addFocusListener(focusListener);
			txtDeslocYDesc.addFocusListener(focusListener);

			chkDesenharDesc.addActionListener(this);
			txtDeslocXDesc.addActionListener(this);
			txtDeslocYDesc.addActionListener(this);

			textArea.setText(relacao.getDescricao());
			textArea.addFocusListener(focusListenerDesc);
			textArea.addKeyListener(keyListener);
			add(BorderLayout.CENTER, textArea);

			Box container = Box.createVerticalBox();
			container.add(criarLinha("label.desloc_x_desc", txtDeslocXDesc));
			container.add(criarLinha("label.desloc_y_desc", txtDeslocYDesc));
			container.add(criarLinha("label.desenhar_desc", chkDesenharDesc));

			add(BorderLayout.SOUTH, container);
		}

		private FocusListener focusListener = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				actionPerformed(new ActionEvent(e.getSource(), 0, null));
			}
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			TextField txt = null;

			if (e.getSource() instanceof TextField) {
				txt = (TextField) e.getSource();
			}

			if (txtDeslocXDesc == e.getSource()) {
				relacao.deslocamentoXDesc = Util.getInt(txt.getText(), relacao.deslocamentoXDesc);
				Formulario.macro.deslocarXIdDescricao(relacao.deslocamentoXDesc);

			} else if (txtDeslocYDesc == e.getSource()) {
				relacao.deslocamentoYDesc = Util.getInt(txt.getText(), relacao.deslocamentoYDesc);
				Formulario.macro.deslocarYIdDescricao(relacao.deslocamentoYDesc);

			} else if (chkDesenharDesc == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				relacao.setDesenharDescricao(chk.isSelected());
				Formulario.macro.desenharIdDescricao(chk.isSelected());
			}

			superficie.repaint();
		}

		private FocusListener focusListenerDesc = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				keyListener.keyReleased(null);
				superficie.repaint();
			}
		};

		private KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				relacao.setDescricao(textArea.getText());
			}
		};
	}

	private class PanelCorFonte extends PanelBorder implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final JColorChooser colorChooser;

		PanelCorFonte() {
			colorChooser = new JColorChooser(relacao.getCorFonte());
			colorChooser.getSelectionModel().addChangeListener(this);
			add(BorderLayout.CENTER, colorChooser);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			relacao.setCorFonte(colorChooser.getColor());
			Formulario.macro.corFonte(relacao.getCorFonte());
			superficie.repaint();
		}
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
			Formulario.macro.cor(relacao.getCor());
			superficie.repaint();
		}
	}

	private class PanelLado extends Panel {
		private static final long serialVersionUID = 1L;
		private final CheckBox chkPonto = new CheckBox("label.ponto");
		private final boolean origem;

		PanelLado(boolean origem) {
			super(new GridLayout(5, 1));
			this.origem = origem;
			chkPonto.setSelected(origem ? relacao.isPontoOrigem() : relacao.isPontoDestino());
			chkPonto.addActionListener(pontoListener);

			Label label = new Label();
			label.setText(origem ? relacao.getOrigem().getId() : relacao.getDestino().getId());

			if (origem) {
				add(new PanelTitulo("label.origem"));
			} else {
				add(new PanelTitulo("label.destino"));
			}

			add(new JSeparator());
			add(new PanelCenter(label));
			add(new PanelCenter(new PanelObjeto(origem ? relacao.getOrigem() : relacao.getDestino())));
			add(new PanelCenter(chkPonto));
		}

		private ActionListener pontoListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (origem) {
					Formulario.macro.pontoOrigem(chkPonto.isSelected());
					relacao.setPontoOrigem(chkPonto.isSelected());
				} else {
					Formulario.macro.pontoDestino(chkPonto.isSelected());
					relacao.setPontoDestino(chkPonto.isSelected());
				}

				superficie.repaint();
			}
		};
	}

	private class PanelTitulo extends Panel {
		private static final long serialVersionUID = 1L;

		PanelTitulo(String chave) {
			setLayout(new GridBagLayout());
			add(new Label(chave));
		}
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
			addTab("label.cor_fonte", new PanelCorFonte());
		}
	}
}