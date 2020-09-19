package br.com.persist.plugins.variaveis;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Variavel {
	private final String nome;
	private String valor;

	public Variavel(String nome) {
		this(nome, null);
	}

	public Variavel(String nome, String valor) {
		if (Util.estaVazio(nome)) {
			throw new IllegalArgumentException("Nome nulo.");
		}
		this.valor = valor;
		this.nome = nome;
	}

	public Variavel clonar(String novoNome) {
		return new Variavel(novoNome, valor);
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(Constantes.VARIAVEL);
		util.atributo("nome", Util.escapar(nome));
		util.fecharTag();

		util.abrirTag2(Constantes.VALOR);
		util.conteudo(Util.escapar(getValor())).ql();
		util.finalizarTag(Constantes.VALOR);

		util.finalizarTag(Constantes.VARIAVEL);
	}

	public String getNome() {
		return nome;
	}

	public boolean isValido() {
		return !Util.estaVazio(nome) && !Util.estaVazio(valor);
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		if (Util.estaVazio(valor)) {
			valor = Constantes.VAZIO;
		}

		return valor;
	}

	public int getInteiro(int padrao) {
		if (Util.estaVazio(valor)) {
			return padrao;
		}

		try {
			return Integer.parseInt(valor.trim());
		} catch (Exception e) {
			return padrao;
		}
	}
}