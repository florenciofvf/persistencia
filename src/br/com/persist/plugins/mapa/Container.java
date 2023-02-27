package br.com.persist.plugins.mapa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.mapa.organiza.Organizador;

public abstract class Container {
	private final Set<String> nomeReferencias;
	private final List<Container> referencias;
	private final List<Atributo> atributos;
	private final List<Container> filhos;
	private Organizador organizador;
	protected final String nome;
	protected Container pai;

	public Container(String nome) {
		if (Util.estaVazio(nome)) {
			throw new IllegalArgumentException("Nome do container vazio.");
		}
		nomeReferencias = new LinkedHashSet<>();
		referencias = new ArrayList<>();
		atributos = new ArrayList<>();
		filhos = new ArrayList<>();
		this.nome = nome;
	}

	public Container getPai() {
		return pai;
	}

	public List<Container> getFilhos() {
		return filhos;
	}

	public List<Container> getReferencias() {
		return referencias;
	}

	public String getNome() {
		return nome;
	}

	public int getQtdFilhos() {
		return filhos.size();
	}

	public int getQtdFilhosRef() {
		return referencias.size();
	}

	public int getQtdAtributos() {
		return atributos.size();
	}

	public void adicionarAtributo(Atributo atributo) {
		atributos.add(atributo);
	}

	public Atributo getAtributo(String nome) {
		for (Atributo a : atributos) {
			if (a.getNome().equalsIgnoreCase(nome)) {
				return a;
			}
		}
		return null;
	}

	public List<Atributo> getAtributos() {
		return atributos;
	}

	public void excluirAtributo(Atributo atributo) {
		atributos.remove(atributo);
	}

	public void resolverReferencias() {
		Iterator<Container> iterator = filhos.iterator();
		while (iterator.hasNext()) {
			Container obj = iterator.next();
			obj.resolverReferencias();
		}

		Iterator<String> it = nomeReferencias.iterator();
		while (it.hasNext()) {
			String nomeRef = it.next();
			Container parent = getContainerVertical(nomeRef);
			if (parent != null) {
				throw new IllegalStateException(
						"Um container pai nao pode ser referenciado no filho. Existe a propriedade obj.pai. [" + nomeRef
								+ "]");
			}
			Container obj = getContainerHorizontal(nomeRef);
			if (obj == null) {
				throw new IllegalStateException("Referencia nao resolvida: " + nomeRef);
			}
			adicionarFilhoRef(obj);
			it.remove();
		}
	}

	public boolean isRef() {
		return isReferencia(nome);
	}

	public static boolean isReferencia(String nome) {
		return "ref".equalsIgnoreCase(nome);
	}

	public void adicionarFilhoRef(Container container) {
		referencias.add(container);
	}

	public void adicionarFilho(Container container) {
		if (container.isRef()) {
			Atributo atributo = container.getAtributo("obj");
			if (atributo == null) {
				throw new IllegalStateException("Referencia sem o atributo obj.");
			}
			nomeReferencias.add(atributo.getValor());
			return;
		}
		if (container.pai != null) {
			container.pai.excluirFilho(container);
		}
		container.pai = this;
		filhos.add(container);
	}

	public void excluirFilho(Container container) {
		if (container.isRef()) {
			return;
		}
		container.pai = null;
		filhos.remove(container);
	}

	public void excluirFilhoRef(Container container) {
		referencias.remove(container);
	}

	private Container getContainerVertical(String nome) {
		Container obj = this;
		while (obj.pai != null) {
			if (obj.nome.equalsIgnoreCase(nome)) {
				return obj;
			}
			obj = obj.pai;
		}
		if (obj.nome.equalsIgnoreCase(nome)) {
			return obj;
		}
		return null;
	}

	private Container getContainerHorizontal(String nome) {
		Container obj = this;
		while (obj != null) {
			Container c = obj.getFilho(nome);
			if (c != null) {
				return c;
			}
			obj = obj.pai;
		}
		return null;
	}

	public Container getFilho(String nome) {
		for (Container obj : filhos) {
			if (obj.nome.equalsIgnoreCase(nome)) {
				return obj;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Container other = (Container) obj;
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equalsIgnoreCase(other.nome)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return nome;
	}

	public Organizador getOrganizador() {
		return organizador;
	}

	public void setOrganizador(Organizador organizador) {
		this.organizador = organizador;
	}
}