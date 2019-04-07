package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

public abstract class AbstratoDialogo extends JDialog {
	private static final long serialVersionUID = 1L;

	public AbstratoDialogo(Dialog dialog, String titulo) {
		super(dialog, titulo, true);
		inicializar();
		setLocationRelativeTo(dialog);
	}

	public AbstratoDialogo(Frame frame, String titulo) {
		super(frame, titulo, true);
		inicializar();
		setLocationRelativeTo(frame);
	}

	private void inicializar() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(1000, 600);
		setActionESC();
	}

	private void setActionESC() {
		JComponent component = (JComponent) getContentPane();

		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");

		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};

		ActionMap actionMap = component.getActionMap();
		actionMap.put("esc", action);
	}
}