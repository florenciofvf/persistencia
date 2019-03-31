package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.comp.Button;
import br.com.persist.comp.TextArea;
import br.com.persist.comp.TextField;
import br.com.persist.util.Action;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class ComplementoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private TextField txtComplemento;

	public ComplementoDialogo(Dialog dialog, Objeto objeto, TextField txtComplemento) {
		super(dialog, objeto.getId(), false);
		this.txtComplemento = txtComplemento;
		montarLayout();
		setVisible(true);
	}

	public ComplementoDialogo(Frame frame, Objeto objeto, TextField txtComplemento) {
		super(frame, objeto.getId(), false);
		this.txtComplemento = txtComplemento;
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
		textArea.setText(txtComplemento.getText());
	}

	protected void processar() {
		throw new UnsupportedOperationException();
	}

	private class Toolbar extends JToolBar implements ActionListener {
		private static final long serialVersionUID = 1L;

		Toolbar() {
			add(new Button(Action.actionIcon("label.aplicar", Icones.SUCESSO, this)));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			txtComplemento.setText(Util.normalizar(textArea.getText(), true));
			dispose();
		}
	}
}