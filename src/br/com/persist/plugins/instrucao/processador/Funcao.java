package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Funcao {
	private final List<Parametro> parametros;
	private final List<Instrucao> instrucoes;
	private Biblioteca biblioteca;
	private String biblioNativa;
	private final String nome;
	private int indice;

	public Funcao(String nome) {
		this.nome = Objects.requireNonNull(nome);
		parametros = new ArrayList<>();
		instrucoes = new ArrayList<>();
	}

	public Funcao clonar() throws InstrucaoException {
		Funcao funcao = new Funcao(nome);
		funcao.biblioNativa = biblioNativa;
		funcao.biblioteca = biblioteca;
		for (Parametro p : parametros) {
			funcao.addParametro(p.nome);
		}
		for (Instrucao inst : instrucoes) {
			funcao.addInstrucao(inst);
		}
		return funcao;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public String getBiblioNativa() {
		return biblioNativa;
	}

	public void setBiblioNativa(String biblioNativa) {
		this.biblioNativa = biblioNativa;
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

	Instrucao getInstrucao() throws InstrucaoException {
		if (indice >= instrucoes.size()) {
			throw new InstrucaoException("erro.funcao_sem_retorno", nome, biblioteca.getNome());
		}
		Instrucao resp = instrucoes.get(indice);
		indice++;
		return resp;
	}

	public void addParametro(String nome) throws InstrucaoException {
		InstrucaoUtil.checarParametro(nome);
		Parametro param = new Parametro(nome);
		if (!parametros.contains(param)) {
			param.indice = parametros.size();
			parametros.add(param);
		}
	}

	public void setValorParametro(int indice, Object valor) throws InstrucaoException {
		InstrucaoUtil.checarOperando(valor);
		parametros.get(indice).valor = valor;
	}

	public void setValorParametro(String nome, Object valor) throws InstrucaoException {
		int pos = getIndiceParametro(nome);
		setValorParametro(pos, valor);
	}

	public Object getValorParametro(int indice) {
		return parametros.get(indice).valor;
	}

	public Object getValorParametro(String nome) throws InstrucaoException {
		int pos = getIndiceParametro(nome);
		return getValorParametro(pos);
	}

	public int getTotalParametro() {
		return parametros.size();
	}

	private int getIndiceParametro(String nome) throws InstrucaoException {
		for (int i = 0; i < parametros.size(); i++) {
			if (parametros.get(i).nome.equals(nome)) {
				return i;
			}
		}
		throw new InstrucaoException("erro.parametro_inexistente", nome, this.nome, this.nome);
	}

	public void addInstrucao(Instrucao instrucao) throws InstrucaoException {
		if (instrucao != null) {
			if (isNativo()) {
				throw new InstrucaoException("erro.metodo_nativo_add_inst", nome);
			}
			instrucoes.add(instrucao);
		}
	}

	public boolean isNativo() {
		return biblioNativa != null;
	}

	@Override
	public String toString() {
		return (isNativo() ? "nativo " + biblioNativa + " " : "") + nome + "(" + parametros + ")";
	}
}

class Parametro {
	final String nome;
	Object valor;
	int indice;

	public Parametro(String nome) {
		this.nome = nome;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parametro other = (Parametro) obj;
		return Objects.equals(nome, other.nome);
	}

	@Override
	public String toString() {
		return indice + ": " + nome + "=" + valor;
	}
}