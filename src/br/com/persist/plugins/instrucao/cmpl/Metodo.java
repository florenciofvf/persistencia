package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.cmpl.InstrucaoGramatica.Param;

public class Metodo {
	private final Return retorn = new Return();
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

	public void print(PrintWriter pw) {
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

	void montar() throws InstrucaoException {
		if (nativo) {
			return;
		}
		MetodoUtil util = new MetodoUtil(this);
		no = util.montar();
	}

	@Override
	public String toString() {
		return nome + "(" + parametros + ")";
	}
}

class Invocacao extends No {
	public Invocacao(String nome) {
		super(nome);
	}

	@Override
	public int totalInstrucoes() {
		return 1;// TODO
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(nome);// TODO
	}
}