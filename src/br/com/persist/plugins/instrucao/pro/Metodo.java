package br.com.persist.plugins.instrucao.pro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.inst.InstrucaoUtil;

public class Metodo {
	private final List<Instrucao> instrucoes;
	private final List<Param> parametros;
	private final Biblioteca biblioteca;
	private final String biblioNativa;
	private final boolean nativo;
	private final String nome;
	private int indice;

	public Metodo(Biblioteca biblioteca, String nome, boolean nativo, String biblioNativa) {
		this.biblioteca = Objects.requireNonNull(biblioteca);
		this.nome = Objects.requireNonNull(nome);
		this.biblioNativa = biblioNativa;
		parametros = new ArrayList<>();
		instrucoes = new ArrayList<>();
		this.nativo = nativo;
	}

	public Metodo clonar() throws InstrucaoException {
		Metodo metodo = new Metodo(biblioteca, nome, nativo, biblioNativa);
		for (Param p : parametros) {
			metodo.addParam(p.nome);
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

	public String getBiblioNativa() {
		return biblioNativa;
	}

	Instrucao getInstrucao() {
		Instrucao resp = instrucoes.get(indice);
		indice++;
		return resp;
	}

	public void addParam(String nome) throws InstrucaoException {
		InstrucaoUtil.checarParam(nome);
		Param param = new Param(nome);
		if (!parametros.contains(param)) {
			param.indice = parametros.size();
			parametros.add(param);
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

	public Object getValorParam(String nome) throws InstrucaoException {
		int pos = getIndiceParam(nome);
		return getValorParam(pos);
	}

	public int getTotalParam() {
		return parametros.size();
	}

	private int getIndiceParam(String nome) throws InstrucaoException {
		for (int i = 0; i < parametros.size(); i++) {
			if (parametros.get(i).nome.equals(nome)) {
				return i;
			}
		}
		throw new InstrucaoException("erro.parametro_inexistente", nome, this.nome, biblioteca.getNome());
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

	@Override
	public String toString() {
		return (nativo ? "nativo " + biblioNativa + " " : "") + nome + "(" + parametros + ")";
	}
}

class Param {
	final String nome;
	Object valor;
	int indice;

	public Param(String nome) {
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
		return indice + ": " + nome + "=" + valor;
	}
}