package br.com.persist.data;

import java.util.Iterator;

import br.com.persist.assistencia.Constantes;

public class Formatador {
	private Formatador() {
	}

	public static void formatar(Array array, Container c, int tab) {
		c.append(getTab(tab), null);
		append(array, c, tab);
	}

	public static void append(Array array, Container c, int tab) {
		c.append("[" + Constantes.QL, Array.att);
		Iterator<Tipo> it = array.getElementos().iterator();
		if (it.hasNext()) {
			it.next().export(c, tab + 1);
		}
		while (it.hasNext()) {
			c.append("," + Constantes.QL, Array.att);
			it.next().export(c, tab + 1);
		}
		c.append(Constantes.QL, null);
		c.append(getTab(tab) + "]", Array.att);
	}

	public static void formatar(Objeto objeto, Container c, int tab) {
		c.append(getTab(tab), null);
		append(objeto, c, tab);
	}

	public static void append(Objeto objeto, Container c, int tab) {
		c.append("{" + Constantes.QL, Objeto.att);
		Iterator<NomeValor> it = objeto.getAtributos().iterator();
		if (it.hasNext()) {
			NomeValor nv = it.next();
			c.append(getTab(tab + 1) + citar(nv.nome) + ": ", Objeto.att2);
			nv.valor.append(c, tab + 1);
		}
		while (it.hasNext()) {
			NomeValor nv = it.next();
			c.append("," + Constantes.QL, Objeto.att);
			c.append(getTab(tab + 1) + citar(nv.nome) + ": ", Objeto.att2);
			nv.valor.append(c, tab + 1);
		}
		c.append(Constantes.QL, null);
		c.append(getTab(tab) + "}", Objeto.att);
	}

	public static String getTab(int i) {
		StringBuilder sb = new StringBuilder();
		int q = 0;
		while (q < i) {
			sb.append("    ");
			q++;
		}
		return sb.toString();
	}

	public static String citar(String s) {
		return "\"" + s + "\"";
	}
}