package br.com.persist.plugins.instrucao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Metodo {
	private final List<NomeValor> parametros;
	private final List<Instrucao> instrucoes;
	private final Biblioteca biblioteca;
	private final boolean nativo;
	private final String nome;
	private int indice;

	public Metodo(Biblioteca biblioteca, String nome, boolean nativo) {
		this.biblioteca = Objects.requireNonNull(biblioteca);
		this.nome = Objects.requireNonNull(nome);
		parametros = new ArrayList<>();
		instrucoes = new ArrayList<>();
		this.nativo = nativo;
	}

	public Metodo clonar() throws InstrucaoException {
		Metodo metodo = new Metodo(biblioteca, nome, nativo);
		for (NomeValor nv : parametros) {
			metodo.addParam(nv.nome);
		}
		if (!nativo) {
			for (Instrucao inst : instrucoes) {
				metodo.addInstrucao(inst);
			}
		}
		return metodo;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) throws InstrucaoException {
		if (indice < 0 || indice >= instrucoes.size()) {
			throw new InstrucaoException("erro.metodo_set_indice", nome);
		}
		this.indice = indice;
	}

	public String getNome() {
		return nome;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	Instrucao getInstrucao() {
		return instrucoes.get(indice);
	}

	public void addParam(String nome) throws InstrucaoException {
		InstrucaoUtil.checarParam(nome);
		NomeValor nomeValor = new NomeValor(nome);
		if (!parametros.contains(nomeValor)) {
			parametros.add(nomeValor);
		}
	}

	public void setValorParam(int indice, Object valor) throws InstrucaoException {
		InstrucaoUtil.checarOperando(valor);
		parametros.get(indice).valor = valor;
	}

	public void setValorParam(String nome, Object valor) throws InstrucaoException {
		int pos = getIndiceParam(nome);
		setValorParam(pos, valor);
	}

	public Object getValorParam(int indice) {
		return parametros.get(indice).valor;
	}

	public Object getValorParam(String nome) {
		int pos = getIndiceParam(nome);
		return getValorParam(pos);
	}

	private int getIndiceParam(String nome) {
		for (int i = 0; i < parametros.size(); i++) {
			if (parametros.get(i).nome.equals(nome)) {
				return i;
			}
		}
		return -1;
	}

	public void addInstrucao(Instrucao instrucao) throws InstrucaoException {
		if (instrucao != null) {
			if (nativo) {
				throw new InstrucaoException("erro.metodo_nativo_add_inst", nome);
			}
			instrucoes.add(instrucao.clonar(this));
		}
	}

	public boolean isNativo() {
		return nativo;
	}
}

class NomeValor {
	final String nome;
	Object valor;

	public NomeValor(String nome) {
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
		NomeValor other = (NomeValor) obj;
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		return true;
	}
}