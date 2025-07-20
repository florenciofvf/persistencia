package br.com.persist.assistencia;

import java.util.Objects;

public class Text implements Comparable<Text> {
	private final String idStyle;
	private final String content;

	public Text(String idStyle, String content) {
		this.idStyle = idStyle;
		if (content != null) {
			content = Util.replaceCR(content);
		}
		this.content = content;
	}

	public String getIdStyle() {
		return idStyle;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return content;
	}

	@Override
	public int hashCode() {
		return Objects.hash(content);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Text other = (Text) obj;
		return Objects.equals(content, other.content);
	}

	@Override
	public int compareTo(Text o) {
		if (content == null || o.content == null) {
			return -1;
		}
		return content.compareTo(o.content);
	}
}