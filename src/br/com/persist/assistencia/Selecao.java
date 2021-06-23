package br.com.persist.assistencia;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.JTextComponent;

import br.com.persist.componente.Label;

public class Selecao {
	private List<Fragmento> lista = new ArrayList<>();
	final JTextComponent component;
	final String string;
	int indice;

	public Selecao(JTextComponent component, String string) {
		if (!Util.estaVazio(string)) {
			this.string = string.toLowerCase();
		} else {
			this.string = null;
		}
		if (component != null && !Util.estaVazio(component.getText())) {
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
		if (component == null) {
			label.setText(Constantes.VAZIO);
			return;
		}
		if (indice < getTotal()) {
			Fragmento frag = lista.get(indice);
			component.setSelectionStart(frag.inicio);
			component.setSelectionEnd(frag.inicio + frag.total);
			component.getCaret().setSelectionVisible(true);
			indice++;
			label.setText(indice + "/" + getTotal());
		} else {
			label.setText(Constantes.VAZIO);
			indice = 0;
		}
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