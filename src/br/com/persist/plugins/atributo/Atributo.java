package br.com.persist.plugins.atributo;

import org.xml.sax.Attributes;

public class Atributo {
	private String absoluto;
	private boolean ignorar;
	private String classe;
	private String nome;

	public String getAbsoluto() {
		return absoluto;
	}

	public void setAbsoluto(String absoluto) {
		this.absoluto = absoluto;
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
		absoluto = attr.getValue("absoluto");
		classe = attr.getValue("classe");
		nome = attr.getValue("nome");
	}

	@Override
	public String toString() {
		return nome;
	}
}