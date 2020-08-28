package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public abstract class AbstratoFormulario extends JFrame {
	private static final long serialVersionUID = 1L;

	public AbstratoFormulario(String titulo) {
		super(titulo);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(Constantes.SIZE);
		Util.configWindowC(this);
		setActionESC();
		configurar();
	}

	private void setActionESC() {
		JComponent component = (JComponent) getContentPane();

		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Constantes.ESC);

		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};

		ActionMap actionMap = component.getActionMap();
		actionMap.put(Constantes.ESC, action);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				executarAoAbrirForm();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				executarAoFecharForm();
			}
		});
	}

	public void executarAoAbrirForm() {
	}

	public void executarAoFecharForm() {
	}
}