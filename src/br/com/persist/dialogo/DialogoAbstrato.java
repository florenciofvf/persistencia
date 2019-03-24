package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import br.com.persist.comp.Button;
import br.com.persist.comp.PanelCenter;
import br.com.persist.util.Action;
import br.com.persist.util.Icones;

public abstract class DialogoAbstrato extends JDialog {
	private static final long serialVersionUID = 1L;

	public DialogoAbstrato(Dialog dialog, String titulo, int largura, int altura, boolean btnProcessar) {
		super(dialog, true);
		ini(titulo, largura, altura, btnProcessar);
		setLocationRelativeTo(dialog);
	}

	public DialogoAbstrato(Frame frame, String titulo, int largura, int altura, boolean btnProcessar) {
		super(frame, true);
		ini(titulo, largura, altura, btnProcessar);
		setLocationRelativeTo(frame);
	}

	public DialogoAbstrato(Dialog dialog, String titulo, boolean btnProcessar) {
		this(dialog, titulo, 600, 600, btnProcessar);
	}

	public DialogoAbstrato(Frame frame, String titulo, boolean btnProcessar) {
		this(frame, titulo, 600, 600, btnProcessar);
	}

	private void ini(String titulo, int largura, int altura, boolean btnProcessar) {
		Action fecharAcao = Action.actionIcon("label.fechar", Icones.SAIR, e -> dispose());

		PanelCenter botoes = new PanelCenter(new Button(fecharAcao));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(largura, altura);

		if (btnProcessar) {
			Action processarAcao = Action.actionIcon("label.ok", Icones.SUCESSO, e -> processar());
			botoes.add(new Button(processarAcao));
		}

		add(BorderLayout.SOUTH, botoes);
		setTitle(titulo);
		setActionESC();
	}

	protected abstract void processar();

	private void setActionESC() {
		JComponent component = (JComponent) getContentPane();

		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");

		javax.swing.Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				WindowEvent event = new WindowEvent(DialogoAbstrato.this, WindowEvent.WINDOW_CLOSING);
				EventQueue systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
				systemEventQueue.postEvent(event);
			}
		};

		ActionMap actionMap = component.getActionMap();
		actionMap.put("esc", action);
	}
}