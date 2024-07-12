package br.com.persist.plugins.instrucao.compilador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.compilador.expressao.OperadorContexto;

public abstract class Container extends AbstratoContexto {
	private final List<Container> filhos;
	protected boolean negativo;
	protected Container pai;
	protected Token token;

	protected Container() {
		filhos = new ArrayList<>();
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public Container getPai() {
		return pai;
	}

	public List<Container> getFilhos() {
		return filhos;
	}

	public int getSize() {
		return filhos.size();
	}

	public boolean isEmpty() {
		return filhos.isEmpty();
	}

	public Container get(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return filhos.get(indice);
		}
		return null;
	}

	public Container excluir(int indice) {
		Container c = get(indice);
		if (c != null) {
			excluir(c);
		}
		return c;
	}

	public void clear() {
		while (getSize() > 0) {
			excluir(0);
		}
	}

	public Container excluirUltimo() {
		return excluir(getSize() - 1);
	}

	public Container getUltimo() {
		return get(getSize() - 1);
	}

	public void excluir(Container c) {
		if (c.pai == this) {
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

	public void negativar(Contexto c) {
		if (c instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) c;
			negativo = "-".equals(operador.getId());
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}