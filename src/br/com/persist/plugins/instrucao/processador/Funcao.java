package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Funcao {
	private final Map<Integer, InstrucaoItem> instrucoes;
	private final List<Parametro> parametros;
	private InstrucaoItem ponteiro;
	private Biblioteca biblioteca;
	private InstrucaoItem cabeca;
	private String biblioNativa;
	private InstrucaoItem cauda;
	private final String nome;
	private boolean tipoVoid;

	public Funcao(String nome) {
		this.nome = Objects.requireNonNull(nome);
		parametros = new ArrayList<>();
		instrucoes = new HashMap<>();
	}

	public Funcao clonar() throws InstrucaoException {
		Funcao funcao = new Funcao(nome);
		funcao.biblioNativa = biblioNativa;
		funcao.biblioteca = biblioteca;
		funcao.tipoVoid = tipoVoid;
		for (Parametro p : parametros) {
			funcao.addParametro(p.nome);
		}
		InstrucaoItem no = cabeca;
		while (no != null) {
			funcao.addInstrucao(no.instrucao);
			no = no.proximo;
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

	public void setIndice(int indice) throws InstrucaoException {
		InstrucaoItem item = instrucoes.get(indice);
		if (item == null) {
			throw new InstrucaoException("erro.funcao_set_indice", nome, indice);
		}
		ponteiro = item;
	}

	public String getNome() {
		return nome;
	}

	Instrucao getInstrucao() throws InstrucaoException {
		if (ponteiro == null) {
			throw new InstrucaoException("erro.funcao_sem_retorno", nome, biblioteca.getNome());
		}
		Instrucao resp = ponteiro.instrucao;
		ponteiro = ponteiro.proximo;
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
			Parametro item = parametros.get(i);
			if (item.contem(nome)) {
				return i;
			}
		}
		throw new InstrucaoException("erro.parametro_inexistente", nome, this.nome, this.nome);
	}

	public void addInstrucao(Instrucao instrucao) throws InstrucaoException {
		if (instrucao == null) {
			return;
		}
		if (isNativo()) {
			throw new InstrucaoException("erro.funcao_nativa_add_inst", nome);
		}
		InstrucaoItem no = new InstrucaoItem(instrucao);
		instrucoes.put(instrucao.sequencia, no);
		if (cabeca == null) {
			ponteiro = no;
			cabeca = no;
		}
		if (cauda != null) {
			cauda.proximo = no;
		}
		cauda = no;
	}

	public boolean isNativo() {
		return biblioNativa != null;
	}

	public boolean isTipoVoid() {
		return tipoVoid;
	}

	public void setTipoVoid(boolean tipoVoid) {
		this.tipoVoid = tipoVoid;
	}

	@Override
	public String toString() {
		return (isNativo() ? "nativo " + biblioNativa + " " : "") + nome + "(" + parametros + ")";
	}

	public String getInterface() {
		return nome + "(" + param(parametros) + ")";
	}

	private String param(List<Parametro> parametros) {
		StringBuilder sb = new StringBuilder();
		for (Parametro item : parametros) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(item.nome);
		}
		return sb.toString();
	}
}

class Parametro {
	final String depois;
	final String antes;
	final String nome;
	Object valor;
	int indice;

	public Parametro(String nome) {
		this.nome = nome;
		int pos = nome.indexOf(':');
		if (pos != -1) {
			antes = nome.substring(1, pos);
			depois = nome.substring(pos + 1, nome.length() - 1);
		} else {
			depois = null;
			antes = null;
		}
	}

	public boolean contem(String string) {
		if (nome.equals(string)) {
			return true;
		}
		if (antes != null && depois != null) {
			return antes.equals(string) || depois.equals(string);
		}
		return false;
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