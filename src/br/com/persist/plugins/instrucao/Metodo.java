package br.com.persist.plugins.instrucao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.assistencia.Util;

public class Metodo {
	private final List<NomeValor> parametros;
	private final List<Instrucao> instrucoes;
	private final String nome;
	private boolean nativo;
	private int indice;

	public Metodo(String nome) {
		this.nome = Objects.requireNonNull(nome);
		parametros = new ArrayList<>();
		instrucoes = new ArrayList<>();
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public String getNome() {
		return nome;
	}

	Instrucao get() {
		return instrucoes.get(indice);
	}

	public void addParam(String nome) {
		if (!Util.estaVazio(nome)) {
			NomeValor nomeValor = new NomeValor(nome);
			if (!parametros.contains(nomeValor)) {
				parametros.add(nomeValor);
			}
		}
	}

	public void setValorParam(int indice, Object valor) throws InstrucaoException {
		if (InstrucaoUtil.tipoValido(valor)) {
			parametros.get(indice).valor = valor;
		} else {
			throw new InstrucaoException("erro.valor_invalido_param", indice, nome);
		}
	}

	public void addInstrucao(Instrucao instrucao) {
		if (instrucao != null) {
			instrucoes.add(instrucao);
		}
	}

	public boolean isNativo() {
		return nativo;
	}

	public void setNativo(boolean nativo) {
		this.nativo = nativo;
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