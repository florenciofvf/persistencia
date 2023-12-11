package br.com.persist.assistencia;

public class StringPool {
	private final StringBuilder sb;

	public StringPool() {
		sb = new StringBuilder();
	}

	public int length() {
		return sb.length();
	}

	public StringPool tab() {
		return tab(1);
	}

	public StringPool tab(int i) {
		append(i, Constantes.TAB);
		return this;
	}

	public StringPool ql() {
		return ql(1);
	}

	public StringPool ql(int i) {
		append(i, Constantes.QL);
		return this;
	}

	public StringPool append(String string) {
		append(1, string);
		return this;
	}

	private void append(int i, String s) {
		int c = 0;
		while (c < i) {
			sb.append(s);
			c++;
		}
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}