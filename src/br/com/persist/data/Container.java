package br.com.persist.data;

import javax.swing.text.AttributeSet;

@FunctionalInterface
public interface Container {
	public void append(String string, AttributeSet attSet);
}