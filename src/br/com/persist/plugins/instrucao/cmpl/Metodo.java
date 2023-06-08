package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class Metodo {
	protected final Return retorn = new Return();
	private final List<No> parametros;
	private final List<Atom> atoms;
	private final String nome;
	private boolean nativo;
	private No no;

	public Metodo(String nome) {
		parametros = new ArrayList<>();
		atoms = new ArrayList<>();
		this.nome = nome;
	}

	public No getNo() {
		return no;
	}

	public void setNo(No no) {
		this.no = no;
	}

	public List<No> getParametros() {
		return parametros;
	}

	public void addParam(No no) {
		if (no instanceof Param) {
			parametros.add(no);
		}
	}

	public String getNome() {
		return nome;
	}

	public boolean isNativo() {
		return nativo;
	}

	public void setNativo(boolean nativo) {
		this.nativo = nativo;
	}

	public List<Atom> getAtoms() {
		return atoms;
	}

	public void addAtom(Atom atom) {
		if (atom != null) {
			atoms.add(atom);
		}
	}

	void montarEstrutura() throws InstrucaoException {
		if (nativo) {
			return;
		}
		MetodoUtil util = new MetodoUtil(this);
		no = util.montar();
	}

	void finalizar() throws InstrucaoException {
		if (nativo) {
			return;
		}
		no.normalizarEstrutura(this);
		retorn.normalizarEstrutura(this);
		AtomicInteger atomic = new AtomicInteger(0);
		no.indexar(atomic);
		retorn.indexar(atomic);
		no.configurarDesvio();
		retorn.configurarDesvio();
	}

	public void print(PrintWriter pw) throws InstrucaoException {
		String prefixo = nativo ? InstrucaoConstantes.PREFIXO_METODO_NATIVO : InstrucaoConstantes.PREFIXO_METODO;
		pw.println(prefixo + nome);
		for (No n : parametros) {
			n.print(pw);
		}
		if (nativo) {
			return;
		}
		no.print(pw);
		retorn.print(pw);
	}

	public Return getReturn() {
		return retorn;
	}

	@Override
	public String toString() {
		return nome + "(" + parametros + ")";
	}
}