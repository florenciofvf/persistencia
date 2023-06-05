package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Metodo {
	private final List<No> parametros;
	private final String nome;
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
		if(no instanceof Param) {
			parametros.add(no);
		}
	}

	public String getNome() {
		return nome;
	}

	public void print(PrintWriter pw) {
		no.print(pw);
		pw.println("return");
	}
}