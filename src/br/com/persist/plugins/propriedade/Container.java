package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.marca.XMLUtil;

public abstract class Container {
	private final List<Container> filhos;
	private final String nome;
	protected Container pai;
	private String valor;

	protected Container(String nome) {
		filhos = new ArrayList<>();
		this.nome = nome;
	}

	public Container getPai() {
		return pai;
	}

	public void setPai(Container pai) {
		this.pai = pai;
	}

	public String getNome() {
		return nome;
	}

	public String getValor() {
		if (valor == null) {
			valor = "";
		}
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public List<Container> getFilhos() {
		return filhos;
	}

	public void excluir(Container c) {
		if (c == this || c.pai == this) {
			filhos.remove(c);
			c.pai = null;
		}
	}

	public void adicionar(Container c) {
		if (c.pai != null) {
			c.pai.excluir(c);
		}
		filhos.add(c);
		c.pai = this;
	}

	public void salvar(Container pai, XMLUtil util) {
	}
}