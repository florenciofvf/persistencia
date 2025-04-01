package br.com.persist.assistencia;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import br.com.persist.componente.Label;

public class Selecao implements Busca {
	private List<Fragmento> lista = new ArrayList<>();
	final JTextComponent component;
	final String string;
	int indice;

	public Selecao(JTextComponent component, String string) {
		if (!Util.isEmpty(string)) {
			this.string = string.toLowerCase();
		} else {
			this.string = null;
		}
		if (component != null && !Util.isEmpty(component.getText())) {
			this.component = component;
		} else {
			this.component = null;
		}
		if (this.component != null && this.string != null) {
			inicializar();
		}
	}

	private void inicializar() {
		String sequencia = component.getText().toLowerCase();
		int pos = sequencia.indexOf(string);
		while (pos != -1) {
			lista.add(new Fragmento(pos, string.length()));
			pos = sequencia.indexOf(string, pos + string.length());
		}
	}

	public boolean igual(String string) {
		return this.string == null ? string == null : this.string.equalsIgnoreCase(string);
	}

	public String getString() {
		return string;
	}

	public int getTotal() {
		return lista.size();
	}

	public int getIndice() {
		return indice;
	}

	public void selecionar(Label label) {
		if (label == null) {
			return;
		}
		if (component == null) {
			label.limpar();
			return;
		}
		if (indice < getTotal()) {
			Fragmento frag = lista.get(indice);
			component.setSelectionStart(frag.inicio);
			component.setSelectionEnd(frag.inicio + frag.total);
			component.getCaret().setSelectionVisible(true);
			indice++;
			label.setText(indice + "/" + getTotal());
			JScrollPane scroll = getScroll(component);
			if (scroll != null) {
				TextUI textUI = component.getUI();
				try {
					int dot = component.getCaret().getDot();
					if (dot >= 0) {
						Rectangle r = textUI.modelToView(component, dot);
						scroll.scrollRectToVisible(r);
					}
				} catch (BadLocationException ex) {
					//
				}
			}
		} else {
			component.getCaret().setSelectionVisible(false);
			label.limpar();
			indice = 0;
		}
	}

	private static JScrollPane getScroll(JTextComponent comp) {
		Component c = comp;
		while (c != null) {
			if (c instanceof JScrollPane) {
				return (JScrollPane) c;
			}
			c = c.getParent();
		}
		return null;
	}
}

class Fragmento {
	final int inicio;
	final int total;

	public Fragmento(int inicio, int total) {
		this.inicio = inicio;
		this.total = total;
	}
}