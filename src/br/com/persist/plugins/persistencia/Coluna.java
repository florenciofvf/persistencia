package br.com.persist.plugins.persistencia;

import java.util.Map;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.conexao.Conexao;

public class Coluna {
	private String valorAlternativoInsert;
	private String stringComparaRegistro;
	private final boolean colunaInfo;
	private String nomeMetodoSetPre;
	private String nomeMetodoSetPos;
	private final String tipoBanco;
	private final boolean autoInc;
	private final boolean nulavel;
	private final boolean numero;
	private boolean inativoTemp;
	private final boolean chave;
	private final boolean blob;
	private final String nome;
	private final String tipo;
	private final int tamanho;
	private final int indice;
	private String sequencia;
	private String funcao;

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
		sb.append(Constantes.TAB + "@Column(name = " + Util.citar2(nome) + ")" + Constantes.QL);
		String declaracao = "private " + getTipoNormal() + " " + nome.toLowerCase() + ";";
		sb.append(Constantes.TAB + declaracao + Constantes.QL);
		sb.append(Constantes.TAB + Util.completar("", declaracao.length(), '-') + Constantes.QL);

		if (!Util.isEmpty(funcao)) {
			sb.append("FUNCAO: " + funcao + Constantes.QL);
		}
		sb.append("AUTO_INCREMENTO: " + autoInc + Constantes.QL);
		sb.append("NUMERICO: " + numero + Constantes.QL);
		sb.append("TAMANHO: " + tamanho + Constantes.QL);
		sb.append("NULAVEL: " + nulavel + Constantes.QL);
		sb.append("COLUNA: " + tipoBanco + Constantes.QL);
		sb.append("INDICE: " + indice + Constantes.QL);
		sb.append("CHAVE: " + chave + Constantes.QL);
		sb.append("NOME: " + nome + Constantes.QL);
		sb.append("TIPO: " + tipo + Constantes.QL);
		sb.append("BLOB: " + blob + Constantes.QL);
		return sb.toString();
	}

	public boolean isValidoConsulta() {
		return !isBlob() && !isColunaInfo();
	}

	public boolean isColunaInfo() {
		return colunaInfo;
	}

	public String getSequencia() {
		return sequencia;
	}

	public String getTipoBanco() {
		return tipoBanco;
	}

	public boolean isNaoChave() {
		return !chave;
	}

	public boolean isAutoInc() {
		return autoInc;
	}

	public boolean isNulavel() {
		return nulavel;
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

	public String getTipoNormal() {
		int pos = tipo.lastIndexOf('.');
		return pos != -1 ? tipo.substring(pos + 1) : tipo;
	}

	public int getIndice() {
		return indice;
	}

	@Override
	public String toString() {
		return nome;
	}

	public void set(Object o, StringBuilder builder) {
		if (o == null || nomeMetodoSetPre == null || nomeMetodoSetPos == null) {
			return;
		}
		String s = o.toString();
		if (numero) {
			builder.append("item." + nomeMetodoSetPre + s + nomeMetodoSetPos + ";" + Constantes.QL);
		} else {
			builder.append("item." + nomeMetodoSetPre + Util.citar2(s) + nomeMetodoSetPos + ";" + Constantes.QL);
		}
	}

	public String get(Object o, Conexao conexao) {
		if (o == null) {
			return Constantes.VAZIO;
		}
		String s = o.toString();
		if (numero) {
			return get(conexao, s);
		}
		while (s.length() > 0 && s.charAt(0) == '\'') {
			s = s.substring(1, s.length());
		}
		while (s.length() > 0 && s.charAt(s.length() - 1) == '\'') {
			s = s.substring(0, s.length() - 1);
		}
		return get(conexao, "'" + s + "'");
	}

	private String get(Conexao conexao, String string) {
		if (conexao == null) {
			return string;
		}
		String funcaoMap = conexao.getMapaTiposFuncoes().get(tipo.toLowerCase());
		if (Util.isEmpty(funcaoMap)) {
			return string;
		}
		return Util.replaceAll(funcaoMap, Constantes.SEP + "valor" + Constantes.SEP, string);
	}

	public void setSequencia(String sequencia) {
		this.sequencia = sequencia;
	}

	public String getFuncao() {
		return funcao;
	}

	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}

	public void configFuncao(Map<String, String> funcoes) {
		if (funcoes != null) {
			funcao = funcoes.get(tipo.toLowerCase());
		}
	}

	public String getValorAlternativoInsert() {
		return valorAlternativoInsert;
	}

	public void setValorAlternativoInsert(String valorAlternativoInsert) {
		this.valorAlternativoInsert = valorAlternativoInsert;
	}

	public boolean isInativoTemp() {
		return inativoTemp;
	}

	public void setInativoTemp(boolean inativoTemp) {
		this.inativoTemp = inativoTemp;
	}

	public String getStringComparaRegistro() {
		return stringComparaRegistro;
	}

	public void setStringComparaRegistro(String stringComparaRegistro) {
		this.stringComparaRegistro = stringComparaRegistro;
	}

	public void setNomeMetodoSet(String nomeMetodoSet) {
		if (Util.isEmpty(nomeMetodoSet)) {
			nomeMetodoSetPre = null;
			nomeMetodoSetPos = null;
		} else {
			final String chaves = "{}";
			int pos = nomeMetodoSet.indexOf(chaves);
			if (pos == -1) {
				nomeMetodoSetPre = nomeMetodoSet + "(";
				nomeMetodoSetPos = ")";
			} else {
				nomeMetodoSetPre = nomeMetodoSet.substring(0, pos);
				nomeMetodoSetPos = nomeMetodoSet.substring(pos + chaves.length());
			}
		}
	}
}