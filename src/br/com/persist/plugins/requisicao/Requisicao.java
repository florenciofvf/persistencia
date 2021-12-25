package br.com.persist.plugins.requisicao;

import java.util.Objects;

import br.com.persist.parser.Tipo;

public class Requisicao {
	private final Tipo tipo;
	private String desc;
	private String url;

	public Requisicao(Tipo tipo) {
		Objects.requireNonNull(tipo);
		this.tipo = tipo;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getString() {
		return null;
	}

	public Tipo getTipo() {
		return tipo;
	}
}