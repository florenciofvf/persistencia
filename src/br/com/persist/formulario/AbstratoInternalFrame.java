package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;

import br.com.persist.util.Constantes;

public abstract class AbstratoInternalFrame extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	private boolean abortarFecharComESC;

	public AbstratoInternalFrame(String titulo) {
		super(titulo, true, true, true, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(Constantes.SIZE);
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
				if (abortarFecharComESC) {
					return;
				}

				dispose();
			}
		};

		ActionMap actionMap = component.getActionMap();
		actionMap.put("esc", action);
	}

	public boolean isAbortarFecharComESC() {
		return abortarFecharComESC;
	}

	public void setAbortarFecharComESC(boolean abortarFecharComESC) {
		this.abortarFecharComESC = abortarFecharComESC;
	}
}