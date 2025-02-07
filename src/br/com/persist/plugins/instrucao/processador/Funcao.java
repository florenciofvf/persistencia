package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

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
	private Funcao parent;

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
		funcao.parent = parent;
		for (Parametro p : parametros) {
			funcao.addParametro(p.getNome());
		}
		InstrucaoItem no = cabeca;
		while (no != null) {
			funcao.addInstrucao(no.instrucao);
			no = no.proximo;
		}
		return funcao;
	}

	public Funcao getParent() {
		return parent;
	}

	public void setParent(Funcao parent) {
		this.parent = parent;
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
			throw new InstrucaoException("erro.funcao_sem_instrucao", nome, biblioteca.getNome());
		}
		Instrucao resp = ponteiro.instrucao;
		ponteiro = ponteiro.proximo;
		return resp;
	}

	public void addParametro(String nome) throws InstrucaoException {
		InstrucaoUtil.checarParametro(nome);
		if (contem(nome)) {
			throw new InstrucaoException("erro.parametro_inexistente", nome, this.nome, biblioteca.getNome());
		}
		int pos = nome.indexOf(':');
		Token token = new Token(nome, pos != -1 ? Tipo.LISTA : Tipo.IDENTITY);
		Parametro param = new Parametro(token);
		param.indice = parametros.size();
		parametros.add(param);
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

	public boolean contem(String string) {
		return getParametro(string) != null;
	}

	public Parametro getParametro(String string) {
		for (Parametro item : parametros) {
			if (item.contem(string)) {
				return item;
			}
		}
		return null;
	}

	private int getIndiceParametro(String nome) throws InstrucaoException {
		for (int i = 0; i < parametros.size(); i++) {
			Parametro item = parametros.get(i);
			if (item.contem(nome)) {
				return i;
			}
		}
		throw new InstrucaoException("erro.parametro_inexistente", nome, this.nome, biblioteca.getNome());
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
			sb.append(item.getNome());
		}
		return sb.toString();
	}

	public String getInterfaceInfo() {
		StringBuilder sb = new StringBuilder(getInterface());
		if (tipoVoid) {
			sb.append(" : " + InstrucaoConstantes.VOID);
		}
		if (isNativo()) {
			sb.append(" [nativo]");
		}
		return sb.toString();
	}
}