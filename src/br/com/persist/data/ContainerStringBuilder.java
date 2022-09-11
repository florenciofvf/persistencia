package br.com.persist.data;

import javax.swing.text.AttributeSet;

public class ContainerStringBuilder implements Container {
	private final StringBuilder sb;

	public ContainerStringBuilder(StringBuilder sb) {
		this.sb = sb == null ? new StringBuilder() : sb;
	}

	public ContainerStringBuilder() {
		this(null);
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	@Override
	public void append(String string, AttributeSet attSet) {
		if (string != null) {
			sb.append(string);
		}
	}
}