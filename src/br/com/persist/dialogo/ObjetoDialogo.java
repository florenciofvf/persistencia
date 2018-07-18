package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
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

	private class PanelGeral extends PanelBorder implements ActionListener, FocusListener {
		private static final long serialVersionUID = 1L;
		private TextField txtFiltroInicio = new TextField();
		private CheckBox chkDesenharId = new CheckBox();
		private TextField txtTabela = new TextField();
		private TextField txtChaves = new TextField();
		private TextField txtId = new TextField();
		private TextField txtX = new TextField();
		private TextField txtY = new TextField();
		private Label labelIcone = new Label();

		PanelGeral() {
			txtFiltroInicio.setText(objeto.getFiltroInicio());
			chkDesenharId.setSelected(objeto.isDesenharId());
			txtTabela.setText(objeto.getTabela());
			txtChaves.setText(objeto.getChaves());
			txtId.setText(objeto.getId());
			txtX.setText("" + objeto.x);
			txtY.setText("" + objeto.y);

			txtFiltroInicio.addActionListener(this);
			txtFiltroInicio.addFocusListener(this);
			chkDesenharId.addActionListener(this);
			txtTabela.addActionListener(this);
			txtChaves.addActionListener(this);
			txtTabela.addFocusListener(this);
			txtChaves.addFocusListener(this);
			txtId.addActionListener(this);
			txtId.addFocusListener(this);
			txtX.addActionListener(this);
			txtY.addActionListener(this);
			txtX.addFocusListener(this);
			txtY.addFocusListener(this);

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
			container.add(criarLinha("label.tabela", txtTabela));
			container.add(criarLinha("label.chaves", txtChaves));
			container.add(criarLinha("label.filtro_inicio", txtFiltroInicio));
			container.add(criarLinha("label.desenhar_id", chkDesenharId));

			add(BorderLayout.CENTER, container);
		}

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			actionPerformed(new ActionEvent(e.getSource(), 0, null));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TextField txt = null;

			if (e.getSource() instanceof TextField) {
				txt = (TextField) e.getSource();
			}

			if (txtX == e.getSource()) {
				objeto.x = getInt(txt.getText(), objeto.x);

			} else if (txtY == e.getSource()) {
				objeto.y = getInt(txt.getText(), objeto.y);

			} else if (txtFiltroInicio == e.getSource()) {
				objeto.setFiltroInicio(txt.getText());

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
						superficie.alinhar(objeto);
					}
				}
			} else if (chkDesenharId == e.getSource()) {
				CheckBox chk = (CheckBox) e.getSource();
				objeto.setDesenharId(chk.isSelected());
			}

			superficie.repaint();
		}
	}

	private class PanelDesc extends PanelBorder implements KeyListener {
		private static final long serialVersionUID = 1L;
		private TextArea textArea = new TextArea();

		PanelDesc() {
			textArea.setText(objeto.getDescricao());
			add(BorderLayout.CENTER, textArea);
			textArea.addKeyListener(this);
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			objeto.setDescricao(textArea.getText());
		}
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
			addTab("label.desc", new PanelDesc());
			addTab("label.cor", new PanelCor());
		}
	}
}