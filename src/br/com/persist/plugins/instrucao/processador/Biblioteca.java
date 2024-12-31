package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.assistencia.MetaInfo;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class Biblioteca {
	private final Map<String, Constante> constantes;
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

	public void addConstante(Constante constante) {
		if (constante != null) {
			constantes.put(constante.getNome(), constante);
			constante.setBiblioteca(this);
		}
	}

	public void addFuncao(Funcao funcao) {
		if (funcao != null) {
			funcoes.put(funcao.getNome(), funcao);
			funcao.setBiblioteca(this);
		}
	}

	public boolean contemFuncao(String nome) {
		return funcoes.containsKey(nome);
	}

	public Constante getConstante(String nome) throws InstrucaoException {
		Constante constante = constantes.get(nome);
		if (constante == null) {
			throw new InstrucaoException("erro.constante_inexistente", nome, this.nome);
		}
		return constante;
	}

	public Funcao getFuncao(String nome) throws InstrucaoException {
		Funcao funcao = funcoes.get(nome);
		if (funcao == null) {
			throw new InstrucaoException("erro.funcao_inexistente", nome, this.nome);
		}
		return funcao;
	}

	public void initConstantes() throws InstrucaoException {
		for (Constante constante : constantes.values()) {
			constante.init();
		}
	}

	public List<MetaInfo> getNomeConstantes() {
		List<MetaInfo> lista = new ArrayList<>();
		for (String item : constantes.keySet()) {
			lista.add(new MetaInfo(item, item));
		}
		return lista;
	}

	public List<MetaInfo> getNomeFuncoes() {
		List<MetaInfo> lista = new ArrayList<>();
		for (Funcao item : funcoes.values()) {
			lista.add(new MetaInfo(item.getInterface(), item.getInterfaceInfo()));
		}
		return lista;
	}

	@Override
	public String toString() {
		return nome + " constantes:" + constantes;
	}
}