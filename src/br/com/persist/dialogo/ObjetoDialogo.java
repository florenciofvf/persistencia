package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.com.persist.Objeto;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Label;
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.PanelCenter;
import br.com.persist.comp.TabbedPane;
import br.com.persist.comp.TextArea;
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

	private class PanelGeral extends PanelBorder implements ActionListener {
		private static final long serialVersionUID = 1L;
		private TextField txtComplemento = new TextField();
		private CheckBox chkTransparente = new CheckBox();
		private TextField txtDeslocYId = new TextField();
		private CheckBox chkDesenharId = new CheckBox();
		private TextField txtTabela = new TextField();
		private TextField txtChaves = new TextField();
		private TextField txtId = new TextField();
		private TextField txtX = new TextField();
		private TextField txtY = new TextField();
		private Label labelIcone = new Label();

		PanelGeral() {
			txtDeslocYId.setText("" + objeto.getDeslocamentoYId());
			chkTransparente.setSelected(objeto.isTransparente());
			chkDesenharId.setSelected(objeto.isDesenharId());
			txtComplemento.setText(objeto.getComplemento());
			txtTabela.setText(objeto.getTabela());
			txtChaves.setText(objeto.getChaves());
			txtId.setText(objeto.getId());
			txtX.setText("" + objeto.x);
			txtY.setText("" + objeto.y);

			txtComplemento.addFocusListener(focusListener);
			txtDeslocYId.addFocusListener(focusListener);
			txtTabela.addFocusListener(focusListener);
			txtChaves.addFocusListener(focusListener);
			txtId.addFocusListener(focusListener);
			txtX.addFocusListener(focusListener);
			txtY.addFocusListener(focusListener);

			chkTransparente.addActionListener(this);
			txtComplemento.addActionListener(this);
			chkDesenharId.addActionListener(this);
			txtDeslocYId.addActionListener(this);
			txtTabela.addActionListener(this);
			txtChaves.addActionListener(this);
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
			container.add(criarLinha("label.icone", panelIcone));
			container.add(criarLinha("label.id", txtId));
			container.add(criarLinha("label.x", txtX));
			container.add(criarLinha("label.y", txtY));
			container.add(criarLinha("label.desloc_y_id", txtDeslocYId));
			container.add(criarLinha("label.tabela", txtTabela));
			container.add(criarLinha("label.chaves", txtChaves));
			container.add(criarLinha("label.complemento", txtComplemento));
			container.add(criarLinha("label.desenhar_id", chkDesenharId));
			container.add(criarLinha("label.transparente", chkTransparente));

			add(BorderLayout.CENTER, container);
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

			if (txtX == e.getSource()) {
				objeto.x = Util.getInt(txt.getText(), objeto.x);

			} else if (txtY == e.getSource()) {
				objeto.y = Util.getInt(txt.getText(), objeto.y);

			} else if (txtDeslocYId == e.getSource()) {
				objeto.setDeslocamentoYId(Util.getInt(txtDeslocYId.getText(), objeto.getDeslocamentoYId()));

			} else if (txtComplemento == e.getSource()) {
				objeto.setComplemento(txt.getText());

			} else if (txtTabela == e.getSource()) {
				objeto.setTabela(txt.getText());

			} else if (txtChaves == e.getSource()) {
				objeto.setChaves(txt.getText());

			} else if (txtId == e.getSource()) {
				String id = txt.getText();

				if (!Util.estaVazio(id)) {
					Objeto obj = new Objeto();
					obj.setId(id);

					if (!superficie.contem(obj)) {
						objeto.setId(id);
						superficie.alinharNome(objeto);
					}
				}
			} else if (chkDesenharId == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setDesenharId(chk.isSelected());

			} else if (chkTransparente == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setTransparente(chk.isSelected());
			}

			superficie.repaint();
		}
	}

	private class PanelDesc extends PanelBorder {
		private static final long serialVersionUID = 1L;
		private final TextArea textArea = new TextArea();

		PanelDesc() {
			textArea.setText(objeto.getDescricao());
			textArea.addKeyListener(keyListener);
			add(BorderLayout.CENTER, textArea);
		}

		private KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				objeto.setDescricao(textArea.getText());
			}
		};
	}

	private class PanelCor extends PanelBorder implements ChangeListener {
		private static final long serialVersionUID = 1L;
		private final JColorChooser colorChooser;

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
		label.setPreferredSize(new Dimension(100, 0));
		label.setMinimumSize(new Dimension(100, 0));

		box.add(label);

		if (componente instanceof CheckBox) {
			box.add(componente);
			box.add(Box.createHorizontalGlue());

		} else if (componente instanceof JPanel) {
			box.add(Box.createHorizontalStrut(3));
			box.add(componente);
			box.add(Box.createHorizontalStrut(2));

		} else {
			box.add(componente);
		}

		return box;
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