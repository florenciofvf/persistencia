package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Metodo {
	private final List<No> parametros;
	private final String nome;
	private List<Atom> atoms;
	private boolean nativo;
	private No no;

	public Metodo(String nome) {
		parametros = new ArrayList<>();
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

	public void setAtoms(List<Atom> atoms) {
		this.atoms = atoms;
	}

	public void print(PrintWriter pw) {
		no.print(pw);
		pw.println("return");
	}

	void criarHierarquia() {
		//
	}

	@Override
	public String toString() {
		return nome + "(" + parametros + ")";
	}
}