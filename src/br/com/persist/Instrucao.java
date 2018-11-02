package br.com.persist;

import br.com.persist.util.Util;
import br.com.persist.util.XMLUtil;

public class Instrucao {
	private final String nome;
	private String valor;

	public Instrucao(String nome) {
		if (Util.estaVazio(nome)) {
			throw new IllegalStateException();
		}

		this.nome = nome;
	}

	public String getValor() {
		if (Util.estaVazio(valor)) {
			valor = "";
		}

		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getNome() {
		return nome;
	}

	public Instrucao clonar() {
		Instrucao obj = new Instrucao(nome);
		obj.setValor(valor);

		return obj;
	}

	public void salvar(XMLUtil util) {
		if (!Util.estaVazio(getValor())) {
			util.abrirTag("instrucao");
			util.atributo("nome", Util.escapar(nome));
			util.fecharTag();

			util.abrirTag2("valor");
			util.conteudo(Util.escapar(getValor())).ql();
			util.finalizarTag("valor");

			util.finalizarTag("instrucao");
		}
	}
}