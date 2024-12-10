package br.com.persist.componente;

import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public abstract class TextArea extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private final JTextArea textAreaInner;

	protected TextArea(String string) {
		textAreaInner = new JTextArea(string);
		setViewportView(textAreaInner);
	}

	protected TextArea() {
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

	@Override
	public void requestFocus() {
		textAreaInner.requestFocus();
	}

	public boolean estaVazio() {
		return Util.isEmpty(getText());
	}

	public String getSelectedText() {
		return textAreaInner.getSelectedText();
	}

	public JTextArea getTextAreaInner() {
		return textAreaInner;
	}

	public String getText() {
		return textAreaInner.getText();
	}

	public void limpar() {
		textAreaInner.setText(Constantes.VAZIO);
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