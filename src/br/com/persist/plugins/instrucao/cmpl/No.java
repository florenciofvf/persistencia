package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class No {
	protected final List<No> nos;
	protected String nome;
	protected int indice;
	protected No parent;

	public No(String nome) {
		nos = new ArrayList<>();
		this.nome = nome;
	}

	public No add(No no) {
		if (no != null) {
			if (no.parent != null) {
				no.parent.remove(no);
			}
			no.parent = this;
			nos.add(no);
		}
		return this;
	}

	public No get(int indice) {
		return nos.get(indice);
	}

	public No getUltimoNo() {
		return nos.get(nos.size() - 1);
	}

	public No excluirUltimoNo() {
		No no = nos.remove(nos.size() - 1);
		no.parent = null;
		return no;
	}

	public No getParent() {
		return parent;
	}

	public No remove(No no) {
		no.parent = null;
		nos.remove(no);
		return this;
	}

	public List<No> getNos() {
		return nos;
	}

	@Override
	public String toString() {
		return nome;
	}

	public abstract void normalizarEstrutura(Metodo metodo) throws InstrucaoException;

	public abstract void indexar(AtomicInteger atomic) throws InstrucaoException;

	public abstract void configurarDesvio() throws InstrucaoException;

	public void configDesvio(Desvio desvio) {
		if (nos.isEmpty()) {
			desvio.salto = indice;
		} else {
			nos.get(0).configDesvio(desvio);
		}
	}

	public No proximoApos(No no) throws InstrucaoException {
		if (no == null) {
			throw new InstrucaoException(nome + " <<< proximoApos(No no) >>> null", false);
		}
		int pos = nos.indexOf(no);
		if (pos == -1) {
			throw new InstrucaoException(nome + " <<< proximoApos(No no) >>> inexistente", false);
		}
		if (pos == nos.size() - 1) {
			return this;
		}
		return nos.get(pos + 1);
	}

	public abstract void print(PrintWriter pw) throws InstrucaoException;

	public void print(PrintWriter pw, String... strings) {
		pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.ESPACO + indice + " -");
		for (String string : strings) {
			pw.print(InstrucaoConstantes.ESPACO + string);
		}
		pw.println();
	}

	public void checarOperandos0() throws InstrucaoException {
		checarOperandos(0);
	}

	public void checarOperandos1() throws InstrucaoException {
		checarOperandos(1);
	}

	public void checarOperandos2() throws InstrucaoException {
		checarOperandos(2);
	}

	public void checarOperandos3() throws InstrucaoException {
		checarOperandos(3);
	}

	public void checarOperandos(int total) throws InstrucaoException {
		if (nos.size() != total) {
			throw new InstrucaoException(nome + " <<< Total de operandos incorreto", false);
		}
	}
}