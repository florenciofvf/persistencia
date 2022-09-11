package br.com.persist.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Array extends Tipo {
	static final MutableAttributeSet att;
	private final List<Tipo> elementos;

	public Array() {
		elementos = new ArrayList<>();
	}

	public void export(Container c, int tab) {
		Formatador.formatar(this, c, tab);
	}

	public List<Tipo> getElementos() {
		return elementos;
	}

	public void addElemento(Tipo tipo) {
		tipo.pai = this;
		elementos.add(tipo);
	}

	public Tipo getElemento(int indice) {
		return elementos.get(indice);
	}

	public void preElemento() throws DataException {
		if (elementos.isEmpty()) {
			throw new DataException("Array virgula");
		}
	}

	@Override
	public String toString() {
		return DataUtil.toString(this);
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, Color.BLACK);
	}
}