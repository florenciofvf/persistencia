package br.com.persist.componente;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public abstract class AbstratoFichario extends JTabbedPane {
	private transient AbstratoFicharioListener listener;
	private static final long serialVersionUID = 1L;

	protected AbstratoFichario() {
		configurar();
	}

	private void configurar() {
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_F), "focus_input_pesquisar");
		getActionMap().put("focus_input_pesquisar", actionFocusPesquisar);
	}

	private transient javax.swing.Action actionFocusPesquisar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (listener != null) {
				listener.focusInputPesquisar(AbstratoFichario.this);
			}
		}
	};

	public static KeyStroke getKeyStrokeCtrl(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
	}

	public AbstratoFicharioListener getListener() {
		return listener;
	}

	public void setListener(AbstratoFicharioListener listener) {
		this.listener = listener;
	}

	public abstract List<Aba> getAbas();

	public FicharioPesquisa getPesquisa(FicharioPesquisa pesquisa, String string, boolean porParte) {
		if (pesquisa == null) {
			return new FicharioPesquisa(this, string, porParte);
		} else if (pesquisa.igual(string, porParte)) {
			return pesquisa;
		}
		return new FicharioPesquisa(this, string, porParte);
	}
}