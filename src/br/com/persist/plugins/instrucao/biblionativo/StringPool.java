package br.com.persist.plugins.instrucao.biblionativo;

public class StringPool {
	private final StringBuilder sb;

	public StringPool() {
		sb = new StringBuilder();
	}

	public void append(Object object) {
		sb.append(object);
	}

	@Override
	public java.lang.String toString() {
		return sb.toString();
	}
}