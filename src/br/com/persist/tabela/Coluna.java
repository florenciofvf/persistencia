package br.com.persist.tabela;

import br.com.persist.util.Constantes;

public class Coluna {
	private final boolean numero;
	private final boolean chave;
	private final boolean blob;
	private final String nome;
	private final String tipo;
	private final int indice;

	public Coluna(String nome, int indice, boolean numero, boolean chave, boolean blob, String tipo) {
		this.indice = indice;
		this.numero = numero;
		this.chave = chave;
		this.nome = nome;
		this.blob = blob;
		this.tipo = tipo;
	}

	public String getDetalhe() {
		StringBuilder sb = new StringBuilder();
		sb.append("    NOME: " + nome + Constantes.QL);
		sb.append("    TIPO: " + tipo + Constantes.QL);
		sb.append("   CHAVE: " + chave + Constantes.QL);
		sb.append("NUMÉRICO: " + numero + Constantes.QL);
		sb.append("  ÍNDICE: " + indice + Constantes.QL);
		sb.append("    BLOB: " + blob);

		return sb.toString();
	}

	public boolean isNaoChave() {
		return !chave;
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

	public boolean isBlob() {
		return blob;
	}

	public String getTipo() {
		return tipo;
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