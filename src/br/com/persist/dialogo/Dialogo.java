package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import br.com.persist.comp.Button;
import br.com.persist.comp.PanelCenter;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;

public abstract class Dialogo extends JDialog {
	private static final long serialVersionUID = 1L;

	public Dialogo(Frame frame, String titulo, int largura, int altura, boolean btnProcessar) {
		super(frame, true);

		PanelCenter botoes = new PanelCenter(new Button(new FecharAcao()));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(largura, altura);

		if (btnProcessar) {
			botoes.add(new Button(new ProcessarAcao()));
		}

		add(BorderLayout.SOUTH, botoes);
		setLocationRelativeTo(frame);
		setActionESC(this);
		setTitle(titulo);
	}

	protected abstract void processar();

	private class FecharAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FecharAcao() {
			super(false, "label.fechar", Icones.SAIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private class ProcessarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ProcessarAcao() {
			super(false, "label.ok", Icones.SUCESSO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			processar();
		}
	}

	private void setActionESC(JDialog dialog) {
		JComponent component = (JComponent) dialog.getContentPane();

		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");

		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				WindowEvent event = new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING);
				EventQueue systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
				systemEventQueue.postEvent(event);
			}
		};

		ActionMap actionMap = component.getActionMap();
		actionMap.put("esc", action);
	}
}