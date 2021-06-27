package br.com.persist.plugins.mapeamento;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Mapeamento {
	private final String nome;
	private String valor;

	public Mapeamento(String nome) {
		this(nome, null);
	}

	public Mapeamento(String nome, String valor) {
		if (Util.estaVazio(nome)) {
			throw new IllegalArgumentException("Nome nulo.");
		}
		this.valor = valor;
		this.nome = nome;
	}

	public Mapeamento clonar(String novoNome) {
		return new Mapeamento(novoNome, valor);
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(MapeamentoConstantes.MAPEAMENTO);
		util.atributo("nome", nome);
		util.fecharTag();
		util.abrirTag2(Constantes.VALOR);
		util.conteudo("<![CDATA[").ql();
		util.tab().conteudo(getValor()).ql();
		util.conteudo("]]>").ql();
		util.finalizarTag(Constantes.VALOR);
		util.finalizarTag(MapeamentoConstantes.MAPEAMENTO);
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