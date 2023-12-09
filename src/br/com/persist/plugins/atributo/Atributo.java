package br.com.persist.plugins.atributo;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

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

	public String gerarDeclaracao() {
		return "\tprivate " + classe + " " + nome + ";" + Constantes.QL;
	}

	public String gerarGet() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tpublic " + classe + " get" + Util.capitalize(nome) + "() {" + Constantes.QL);
		sb.append("\t\treturn " + nome + ";" + Constantes.QL);
		sb.append("\t}" + Constantes.QL);
		return sb.toString();
	}

	public String gerarSet() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tpublic void " + "set" + Util.capitalize(nome) + "(" + classe + " " + nome + ") {" + Constantes.QL);
		sb.append("\t\tthis." + nome + " = " + nome + ";" + Constantes.QL);
		sb.append("\t}" + Constantes.QL);
		return sb.toString();
	}

	@Override
	public String toString() {
		return nome;
	}
}