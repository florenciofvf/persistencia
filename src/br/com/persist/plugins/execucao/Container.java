package br.com.persist.plugins.execucao;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

public class Container {
	private final List<Container> filhos;
	private String string;
	private Container pai;

	public Container() {
		filhos = new ArrayList<>();
	}

	public Container getPai() {
		return pai;
	}

	public void lerAtributos(Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			string = attributes.getValue(i);
		}
	}

	public List<Container> getFilhos() {
		return filhos;
	}

	public Container get(int i) {
		return filhos.get(i);
	}

	public int getIndice(Container container) {
		for (int i = 0; i < filhos.size(); i++) {
			if (filhos.get(i) == container) {
				return i;
			}
		}
		return -1;
	}

	public void adicionar(Container container) {
		if (container != null) {
			if (container.getPai() != null) {
				container.getPai().excluir(container);
			}
			filhos.add(container);
			container.pai = this;
		}
	}

	public void excluir(Container container) {
		if (container.getPai() == this) {
			filhos.remove(container);
		}
	}

	@Override
	public String toString() {
		return string;
	}
}