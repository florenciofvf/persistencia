package br.com.persist.plugins.instrucao.processador;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Biblioteca {
	private final Map<String, Object> constantes;
	public static final String EXTENSAO = ".fvf";
	private final Map<String, Funcao> funcoes;
	private final String nome;

	public Biblioteca(String nome) {
		this.nome = Objects.requireNonNull(nome);
		constantes = new HashMap<>();
		funcoes = new HashMap<>();
	}

	public String getNome() {
		return nome;
	}

	public void addConstante(String nome, Object valor) {
		if (nome != null) {
			constantes.put(nome, valor);
		}
	}

	public void addFuncao(Funcao funcao) {
		if (funcao != null) {
			funcoes.put(funcao.getNome(), funcao);
		}
	}

	public Object getValorConstante(String nome) {
		return constantes.get(nome);
	}

	public Funcao getFuncao(String nome) throws InstrucaoException {
		Funcao funcao = funcoes.get(nome);
		if (funcao == null) {
			throw new InstrucaoException("erro.metodo_inexistente", nome, this.nome);
		}
		return funcao;
	}

	@Override
	public String toString() {
		return nome + " constantes:" + constantes;
	}
}