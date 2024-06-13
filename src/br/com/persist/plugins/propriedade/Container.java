package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public abstract class Container {
	private final List<Container> filhos;
	protected Container pai;

	protected Container() {
		filhos = new ArrayList<>();
	}

	public Container getPai() {
		return pai;
	}

	public void setPai(Container pai) {
		this.pai = pai;
	}

	public List<Container> getFilhos() {
		return filhos;
	}

	public void excluir(Container c) {
		if (c.pai == this) {
			filhos.remove(c);
			c.pai = null;
		}
	}

	public void adicionar(Container c) {
		if (c.pai != null) {
			c.pai.excluir(c);
		}
		filhos.add(c);
		c.pai = this;
	}

	public void print(StyledDocument doc) throws BadLocationException {
	}
}