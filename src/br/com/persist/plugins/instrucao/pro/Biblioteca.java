package br.com.persist.plugins.instrucao.pro;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Biblioteca {
	private final String nome;
	private final Map<String, Metodo> map;

	public Biblioteca(String nome) {
		this.nome = Objects.requireNonNull(nome);
		map = new HashMap<>();
	}

	public String getNome() {
		return nome;
	}

	public Metodo getMetodo(String nome) throws InstrucaoException {
		Metodo metodo = map.get(nome);
		if (metodo == null) {
			throw new InstrucaoException("erro.metodo_inexistente", nome);
		}
		return metodo;
	}

	public void add(Metodo metodo) {
		if (metodo != null) {
			map.put(metodo.getNome(), metodo);
		}
	}
}