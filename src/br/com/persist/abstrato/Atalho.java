package br.com.persist.abstrato;

public class Atalho {
	private final String teclas;
	private final String descricao;
	private final String contexto;

	public Atalho(String teclas, String descricao, String contexto) {
		this.teclas = teclas;
		this.descricao = descricao;
		this.contexto = contexto;
	}

	public String getTeclas() {
		return teclas;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getContexto() {
		return contexto;
	}

	public static Atalho ctrl(char c, String desc, String contexto) {
		return ctrl("" + c, desc, contexto);
	}

	public static Atalho ctrl(String string, String desc, String contexto) {
		return new Atalho("CTRL + " + string, desc, contexto);
	}

	public static Atalho shift(String string, String desc, String contexto) {
		return new Atalho("SHIFT + " + string, desc, contexto);
	}
}