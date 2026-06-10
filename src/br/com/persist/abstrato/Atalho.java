package br.com.persist.abstrato;

public class Atalho {
	private final String teclas;
	private final String descricao;

	public Atalho(String teclas, String descricao) {
		this.teclas = teclas;
		this.descricao = descricao;
	}

	public String getTeclas() {
		return teclas;
	}

	public String getDescricao() {
		return descricao;
	}

	public static Atalho ctrl(char c, String desc) {
		return new Atalho("CTRL + " + c, desc);
	}
}