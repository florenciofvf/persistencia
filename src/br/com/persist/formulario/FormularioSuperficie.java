package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public class FormularioSuperficie extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Container container;

	public FormularioSuperficie(Formulario formulario, Container container, File file) {
		super(file.getName());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		container.setFormularioSuperficie(this);
		this.container = container;
		setSize(1000, 600);
		setActionESC();
		montarLayout();
		configurar();
		setLocationRelativeTo(formulario);
		setVisible(true);
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

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				container.excluido();
			};
		});
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, container);
	}
}