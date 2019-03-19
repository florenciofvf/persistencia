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

import br.com.persist.comp.ScrollPane;
import br.com.persist.util.Mensagens;

public class FormularioDesktop extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Desktop desktop;

	public FormularioDesktop(Formulario formulario) {
		super(Mensagens.getString("label.persistencia"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		desktop = new Desktop(formulario, false);
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
			@Override
			public void windowOpened(WindowEvent e) {
				desktop.distribuir();
			}
		});
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, new ScrollPane(desktop));
	}

	public Desktop getDesktop() {
		return desktop;
	}
}