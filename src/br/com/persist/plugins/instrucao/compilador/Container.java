package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;

public abstract class Container extends AbstratoContexto {
	protected final List<Container> componentes;
	protected NegativoContexto negativo;
	protected Contexto contexto;
	protected Container pai;
	protected Token token;
	protected int indice;

	protected Container() {
		componentes = new ArrayList<>();
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

	public List<Container> getComponentes() {
		return componentes;
	}

	public int getSize() {
		return componentes.size();
	}

	public boolean isEmpty() {
		return componentes.isEmpty();
	}

	public Container get(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return componentes.get(indice);
		}
		return null;
	}

	public Container excluir(int indice) {
		Container c = get(indice);
		if (c != null) {
			return excluir(c);
		}
		return null;
	}

	public void clear() {
		while (getSize() > 0) {
			excluir(0);
		}
	}

	public Container excluirUltimo() {
		return excluir(getSize() - 1);
	}

	public Container getPrimeiro() {
		return get(0);
	}

	public Container getUltimo() {
		return get(getSize() - 1);
	}

	public Container excluir(Container c) {
		boolean excluido = false;
		if (c.pai == this) {
			excluido = componentes.remove(c);
			c.pai = null;
		}
		return excluido ? c : null;
	}

	public void adicionar(Container c) {
		if (c.pai != null) {
			c.pai.excluir(c);
		}
		componentes.add(c);
		c.pai = this;
	}

	public void negativar(Contexto c) {
		if (c instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) c;
			if ("-".equals(operador.getId())) {
				negativo = new NegativoContexto();
			}
		}
	}

	public void indexar(AtomicInteger atomic) {
		for (Container c : componentes) {
			c.indexar(atomic);
		}
		if (negativo != null) {
			negativo.indexar(atomic);
		}
	}

	public void configPontoDesvio() {
		for (Container c : componentes) {
			c.configPontoDesvio();
		}
	}

	public void salvar(PrintWriter pw) {
		for (Container c : componentes) {
			c.salvar(pw);
		}
	}

	public void salvarNegativo(PrintWriter pw) {
		if (negativo != null) {
			negativo.salvar(pw);
		}
	}

	void print(PrintWriter pw, String... strings) {
		pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.ESPACO + indice + " -");
		for (String string : strings) {
			pw.print(InstrucaoConstantes.ESPACO + string);
		}
		pw.println();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}