package br.com.persist.parser;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;

public abstract class Tipo {
	Tipo pai;

	public void toString(StringBuilder sb, boolean comTab, int tab) {
		if (comTab) {
			sb.append(getTab(tab));
		}
	}

	public void toString(AbstractDocument doc, boolean comTab, int tab) throws BadLocationException {
		if (comTab) {
			String s = getTab(tab);
			doc.insertString(doc.getLength(), s, null);
		}
	}

	public void insert(AbstractDocument doc, String s, MutableAttributeSet att) throws BadLocationException {
		doc.insertString(doc.getLength(), s, att);
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