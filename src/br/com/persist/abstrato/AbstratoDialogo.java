package br.com.persist.abstrato;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import br.com.persist.componente.Janela;
import br.com.persist.util.Constantes;

public abstract class AbstratoDialogo extends JDialog implements Janela {
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
		setSize(Constantes.SIZE);
		setActionESC();
		configurar();
	}

	@Override
	public final void fechar() {
		dispose();
	}

	private void setActionESC() {
		JComponent component = (JComponent) getContentPane();

		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Constantes.ESC);

		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				fechar();
			}
		};

		ActionMap actionMap = component.getActionMap();
		actionMap.put(Constantes.ESC, action);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				executarAoAbrirDialog();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				executarAoFecharDialog();
			}
		});
	}

	public void executarAoAbrirDialog() {
	}

	public void executarAoFecharDialog() {
	}
}