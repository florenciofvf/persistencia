package br.com.persist.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
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

	@Override
	public void append(Container c, int tab) {
		Formatador.append(this, c, tab);
	}

	public Object[] converter(Class<?> classe) {
		return Conversor.converter(this, classe);
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

	@Override
	public boolean contem(String string) {
		return false;
	}

	@Override
	public Tipo clonar() {
		Array array = new Array();
		for (Tipo tipo : elementos) {
			array.addElemento(tipo.clonar());
		}
		return array;
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, Color.BLACK);
	}

	public void filtrarComAtributos(String[] atts, String valorAtributo) {
		Iterator<Tipo> it = elementos.iterator();
		while (it.hasNext()) {
			Tipo tipo = it.next();
			if (tipo instanceof Objeto) {
				Objeto objeto = Filtro.comAtributos((Objeto) tipo, atts, valorAtributo);
				if (objeto == null) {
					it.remove();
				} else {
					objeto.filtrarComAtributos(atts, valorAtributo);
				}
			} else if (tipo instanceof Array) {
				((Array) tipo).filtrarComAtributos(atts, valorAtributo);
			}
		}
	}

	public void filtrarSemAtributos(String[] atts) {
		Iterator<Tipo> it = elementos.iterator();
		while (it.hasNext()) {
			Tipo tipo = it.next();
			if (tipo instanceof Objeto) {
				Objeto objeto = Filtro.semAtributos((Objeto) tipo, atts);
				if (objeto == null) {
					it.remove();
				} else {
					objeto.filtrarSemAtributos(atts);
				}
			} else if (tipo instanceof Array) {
				((Array) tipo).filtrarSemAtributos(atts);
			}
		}
	}
}