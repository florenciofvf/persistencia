package br.com.persist.plugins.atributo;

import org.xml.sax.Attributes;

public class Atributo {
	private boolean ignorar;
	private String rotulo;
	private String classe;
	private String nome;

	public String getRotulo() {
		return rotulo;
	}

	public void setRotulo(String rotulo) {
		this.rotulo = rotulo;
	}

	public boolean isIgnorar() {
		return ignorar;
	}

	public void setIgnorar(boolean ignorar) {
		this.ignorar = ignorar;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void aplicar(Attributes attr) {
		rotulo = attr.getValue("rotulo");
		classe = attr.getValue("classe");
		nome = attr.getValue("nome");
	}

	@Override
	public String toString() {
		return nome;
	}
}