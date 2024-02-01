package br.com.persist.componente;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import br.com.persist.assistencia.Constantes;

public class TextField extends JTextField {
	private static final long serialVersionUID = 1L;

	public TextField(int columns) {
		super(columns);
	}

	public TextField(String text) {
		super(text);
	}

	public TextField() {
	}

	public void limpar() {
		setText(Constantes.VAZIO);
	}

	private boolean lower(char c) {
		return c >= 'a' && c <= 'z';
	}

	public void ignorarMinusculo() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				processar(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				processar(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				processar(e);
			}

			public void processar(KeyEvent e) {
				if (lower(e.getKeyChar())) {
					e.consume();
				}
			}
		});
	}

	public void ignorarEspaco() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				processar(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				processar(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				processar(e);
			}

			public void processar(KeyEvent e) {
				if (e.getKeyChar() == ' ') {
					e.consume();
				}
			}
		});
	}

	public void somenteLetras() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char ch = e.getKeyChar();
				setEditable(Character.isLetter(ch) || Character.isISOControl(ch));
			}
		});
	}

	public void somenteLetrasUpper() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				processar(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				processar(e);
			}

			public void processar(KeyEvent e) {
				char ch = e.getKeyChar();
				boolean b = Character.isLetter(ch) || Character.isISOControl(ch);
				setEditable(b);
				if (b && lower(ch)) {
					setText(getText().toUpperCase());
				}
			}
		});
	}

	public void somenteLetrasLower() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				processar(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				processar(e);
			}

			public void processar(KeyEvent e) {
				char ch = e.getKeyChar();
				boolean b = Character.isLetter(ch) || Character.isISOControl(ch);
				setEditable(b);
				if (b && lower(ch)) {
					setText(getText().toLowerCase());
				}
			}
		});
	}
}