package br.com.persist.tabela;

import br.com.persist.util.Constantes;

public class Coluna {
	private final boolean colunaInfo;
	private final String sequencia;
	private final String tipoBanco;
	private final boolean autoInc;
	private final boolean nulavel;
	private final boolean numero;
	private final boolean chave;
	private final boolean blob;
	private final String nome;
	private final String tipo;
	private final int tamanho;
	private final int indice;

	public Coluna(String nome, int indice) {
		this(nome, indice, false, false, false, null, new Config(-1, null, false, false, false, null));
	}

	public Coluna(String nome, int indice, boolean numero, boolean chave, boolean blob, String tipo, Config config) {
		this.autoInc = config.autoIncremento;
		this.colunaInfo = config.colunaInfo;
		this.tipoBanco = config.tipoBanco;
		this.sequencia = config.sequencia;
		this.tamanho = config.tamanho;
		this.nulavel = config.nulavel;
		this.indice = indice;
		this.numero = numero;
		this.chave = chave;
		this.nome = nome;
		this.blob = blob;
		this.tipo = tipo;
	}

	public static class Config {
		private final boolean autoIncremento;
		private final boolean colunaInfo;
		private final String sequencia;
		private final String tipoBanco;
		private final boolean nulavel;
		private final int tamanho;

		public Config(int tamanho, String tipoBanco, boolean nulavel, boolean colunaInfo, boolean autoIncremento,
				String sequencia) {
			this.autoIncremento = autoIncremento;
			this.colunaInfo = colunaInfo;
			this.tipoBanco = tipoBanco;
			this.sequencia = sequencia;
			this.tamanho = tamanho;
			this.nulavel = nulavel;
		}
	}

	public String getDetalhe() {
		StringBuilder sb = new StringBuilder();

		sb.append("AUTO INCR: " + autoInc + Constantes.QL);
		sb.append("COLUNA: " + tipoBanco + Constantes.QL);
		sb.append("TAMANHO: " + tamanho + Constantes.QL);
		sb.append("NUMERICO: " + numero + Constantes.QL);
		sb.append("NULAVEL: " + nulavel + Constantes.QL);
		sb.append("INDICE: " + indice + Constantes.QL);
		sb.append("CHAVE: " + chave + Constantes.QL);
		sb.append("NOME: " + nome + Constantes.QL);
		sb.append("TIPO: " + tipo + Constantes.QL);
		sb.append("BLOB: " + blob + Constantes.QL);

		return sb.toString();
	}

	public boolean isColunaInfo() {
		return colunaInfo;
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
			return Constantes.VAZIO;
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

	public String getSequencia() {
		return sequencia;
	}
}