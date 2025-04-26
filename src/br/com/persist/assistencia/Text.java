package br.com.persist.assistencia;

public class Text {
	private final String idStyle;
	private final String content;

	public Text(String idStyle, String content) {
		this.idStyle = idStyle;
		this.content = content;
	}

	public String getIdStyle() {
		return idStyle;
	}

	public String getContent() {
		return content;
	}
}