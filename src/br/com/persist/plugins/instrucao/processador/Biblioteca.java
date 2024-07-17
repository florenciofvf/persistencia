package br.com.persist.plugins.instrucao.pro;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Biblioteca {
	private final Map<String, Variavel> variaveis;
	public static final String EXTENSAO = ".fvf";
	private final Map<String, Metodo> map;
	private final String nome;

	public Biblioteca(String nome) {
		this.nome = Objects.requireNonNull(nome);
		variaveis = new HashMap<>();
		map = new HashMap<>();
	}

	public String getNome() {
		return nome;
	}

	public void clear() {
		variaveis.clear();
		map.clear();
	}

	public void declararVariavel(String nome, Object valor) throws InstrucaoException {
		Variavel variavel = variaveis.get(nome);
		if (variavel != null) {
			throw new InstrucaoException("erro.variavel_existente", nome, this.nome);
		}
		variaveis.put(nome, new Variavel(nome));
		setValorVariavel(nome, valor);
	}

	public void setValorVariavel(String nome, Object valor) throws InstrucaoException {
		getVariavel(nome).valor = valor;
	}

	public Object getValorVariavel(String nome) throws InstrucaoException {
		return getVariavel(nome).valor;
	}

	private Variavel getVariavel(String nome) throws InstrucaoException {
		Variavel variavel = variaveis.get(nome);
		if (variavel == null) {
			throw new InstrucaoException("erro.variavel_inexistente", nome, this.nome);
		}
		return variavel;
	}

	public Metodo getMetodo(String nome) throws InstrucaoException {
		Metodo metodo = map.get(nome);
		if (metodo == null) {
			throw new InstrucaoException("erro.metodo_inexistente", nome, this.nome);
		}
		return metodo;
	}

	public void add(Metodo metodo) {
		if (metodo != null) {
			map.put(metodo.getNome(), metodo);
		}
	}

	@Override
	public String toString() {
		return nome + " variaveis:" + variaveis;
	}
}

class Variavel {
	final String nome;
	Object valor;

	public Variavel(String nome) {
		this.nome = nome;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		Param other = (Param) obj;
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return nome + "->" + valor;
	}
}