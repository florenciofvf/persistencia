package br.com.persist.tabela;

public class Coluna {
	private final boolean numero;
	private final boolean chave;
	private final String nome;
	private final int indice;

	public Coluna(String nome, int indice, boolean numero, boolean chave) {
		this.indice = indice;
		this.numero = numero;
		this.chave = chave;
		this.nome = nome;
	}

	public boolean isNumero() {
		return numero;
	}

	public boolean isChave() {
		return chave;
	}

	public String getNome() {
		return nome;
	}

	public int getIndice() {
		return indice;
	}

	@Override
	public String toString() {
		return nome;
	}

	public String get(Object o) {
		if (o == null) {
			return "";
		}

		String s = o.toString();

		if (numero) {
			return s;
		}

		while (s.length() > 0 && s.charAt(0) == '\'') {
			s = s.substring(1, s.length());
		}

		while (s.length() > 0 && s.charAt(s.length() - 1) == '\'') {
			s = s.substring(0, s.length() - 1);
		}

		return "'" + s + "'";
	}
}