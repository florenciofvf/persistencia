package br.com.persist.tabela;

import br.com.persist.util.Constantes;

public class Coluna {
	private final String tipoBanco;
	private final boolean numero;
	private final boolean chave;
	private final boolean blob;
	private final String nome;
	private final String tipo;
	private final int tamanho;
	private final int indice;

	public Coluna(String nome, int indice) {
		this(nome, indice, false, false, false, null, -1, null);
	}

	public Coluna(String nome, int indice, boolean numero, boolean chave, boolean blob, String tipo, int tamanho,
			String tipoBanco) {
		this.tipoBanco = tipoBanco;
		this.tamanho = tamanho;
		this.indice = indice;
		this.numero = numero;
		this.chave = chave;
		this.nome = nome;
		this.blob = blob;
		this.tipo = tipo;
	}

	public String getDetalhe() {
		StringBuilder sb = new StringBuilder();

		sb.append("NOME: " + nome + Constantes.QL);
		sb.append("TIPO: " + tipo + Constantes.QL);
		sb.append("BLOB: " + blob + Constantes.QL);
		sb.append("CHAVE: " + chave + Constantes.QL);
		sb.append("INDICE: " + indice + Constantes.QL);
		sb.append("TAMANHO: " + tamanho + Constantes.QL);
		sb.append("NUMERICO: " + numero + Constantes.QL);
		sb.append("COLUNA: " + tipoBanco + Constantes.QL);

		return sb.toString();
	}

	public String getTipoBanco() {
		return tipoBanco;
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

	public int getTamanho() {
		return tamanho;
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