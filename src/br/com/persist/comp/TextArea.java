package br.com.persist.comp;

import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import br.com.persist.util.Util;

public class TextArea extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private final JTextArea textAreaInner;

	public TextArea(String string) {
		textAreaInner = new JTextArea(string);
		setViewportView(textAreaInner);
	}

	public TextArea() {
		textAreaInner = new JTextArea();
		setViewportView(textAreaInner);
	}

	public void insert(int pos, String string) {
		textAreaInner.insert(string, pos);
	}

	public void append(String string) {
		textAreaInner.append(string);
	}

	public void setText(String string) {
		textAreaInner.setText(string);
	}

	public boolean estaVazio() {
		return Util.estaVazio(getText());
	}

	public String getSelectedText() {
		return textAreaInner.getSelectedText();
	}

	public String getText() {
		return textAreaInner.getText();
	}

	public void limpar() {
		textAreaInner.setText("");
	}

	@Override
	public synchronized void addKeyListener(KeyListener l) {
		textAreaInner.addKeyListener(l);
	}

	@Override
	public synchronized void addFocusListener(FocusListener l) {
		textAreaInner.addFocusListener(l);
	}
}