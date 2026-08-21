package br.com.persist.componente;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Preferencias;

public abstract class TextPane extends JTextPane {
	private static final long serialVersionUID = 1L;

	protected TextPane() {
		SwingUtilities.invokeLater(this::aplicarFontePreferencia);
	}

	private void aplicarFontePreferencia() {
		Font font = Preferencias.getFontPreferencia();
		if (font != null) {
			setFont(font);
		}
	}

	public void limpar() {
		setText(Constantes.VAZIO);
	}

	public static void alterarTamanhoFonte(JTextPane textPane, FontListener fontListener) {
		if (textPane == null) {
			return;
		}
		InputMap inputMap = textPane.getInputMap(WHEN_FOCUSED);
		inputMap.put(TextEditor.getKeyStrokeCtrl(KeyEvent.VK_UP), TextEditor.FONTE_MAIOR);
		inputMap.put(TextEditor.getKeyStrokeCtrl(KeyEvent.VK_DOWN), TextEditor.FONTE_MENOR);
		textPane.getActionMap().put(TextEditor.FONTE_MAIOR, new TamanhoFonteAction(textPane, 2, fontListener));
		textPane.getActionMap().put(TextEditor.FONTE_MENOR, new TamanhoFonteAction(textPane, -2, fontListener));
	}

	static class TamanhoFonteAction extends AbstractAction {
		private final transient FontListener fontListener;
		private static final long serialVersionUID = 1L;
		private final JTextPane textPane;
		private final int delta;

		TamanhoFonteAction(JTextPane textPane, int delta, FontListener fontListener) {
			this.fontListener = fontListener;
			this.textPane = textPane;
			this.delta = delta;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			alterarFonte(delta);
		}

		private void alterarFonte(int delta) {
			Font font = textPane.getFont();
			if (font == null) {
				return;
			}
			int size = font.getSize();
			size += delta;
			if (size < 8) {
				size = 8;
			}
			if (size > 50) {
				size = 50;
			}
			font = new Font(font.getName(), font.getStyle(), size);
			textPane.setFont(font);
			if (fontListener != null) {
				fontListener.alteradoPara(size);
			}
		}
	}
}