package br.com.persist.plugins.instrucao.compilador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.compilador.expressao.OperadorContexto;

public abstract class Container extends AbstratoContexto {
	protected static final char[] MODO_INI = { 'I' };
	protected static final char[] MODO_FIN = { 'F' };
	protected static final char[] MODO_SEP = { 'S' };
	protected static final char[] MODO_OPE = { 'O' };
	protected static final char[] MODO_RES = { 'R' };
	protected static final char[] MODO_STR = { 'T' };
	protected static final char[] MODO_NUM = { 'N' };
	protected static final char[] MODO_IDE = { 'Y' };
	private final List<Container> filhos;
	protected boolean finalizado;
	protected boolean negativo;
	protected Container pai;
	protected char[] modo;
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

	public Container get(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return filhos.get(indice);
		}
		return null;
	}

	public Container excluir(int indice) {
		if (indice >= 0 && indice < getSize()) {
			get(indice).pai = null;
			return filhos.remove(indice);
		}
		return null;
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

	public char[] getModo() {
		return modo;
	}

	public void setModo(char[] modo) {
		this.modo = modo;
	}

	protected boolean isModo(char c) {
		for (char ch : modo) {
			if (ch == c) {
				return true;
			}
		}
		return false;
	}

	public void negativar(Contexto c) {
		if (c instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) c;
			negativo = "-".equals(operador.getId());
		}
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}
}