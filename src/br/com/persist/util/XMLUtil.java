package br.com.persist.util;

import java.io.File;
import java.io.PrintWriter;

public class XMLUtil {
	private final PrintWriter pw;
	private int tab = -1;

	public XMLUtil(File file) throws Exception {
		pw = new PrintWriter(file, Constantes.ENCODING);
	}

	public XMLUtil prologo() {
		return print("<?xml").atributo("version", 1.0).atributo("encoding", Constantes.ENCODING).print("?>").ql().ql();
	}

	public XMLUtil atributo(String nome, boolean valor) {
		return print(" " + nome + "=" + citar("" + valor));
	}

	public XMLUtil atributo(String nome, double valor) {
		return print(" " + nome + "=" + citar("" + valor));
	}

	public XMLUtil atributo(String nome, long valor) {
		return print(" " + nome + "=" + citar("" + valor));
	}

	public XMLUtil atributo(String nome, String valor) {
		return print(" " + nome + "=" + citar(valor));
	}

	public XMLUtil abrirTag(String nome) {
		tab++;
		return tabular().print("<" + nome);
	}

	public XMLUtil conteudo(String string) {
		tab++;
		tabular().print(string);
		tab--;
		return this;
	}

	public XMLUtil fecharTag() {
		return print(">").ql();
	}

	public XMLUtil abrirTag2(String nome) {
		return abrirTag(nome).fecharTag();
	}

	public XMLUtil finalizarTag(String nome) {
		tabular().print("</" + nome + ">").ql();
		tab--;
		return this;
	}

	public XMLUtil abrirFinalizarTag(String nome) {
		return abrirTag2(nome).finalizarTag(nome);
	}

	private XMLUtil tabular() {
		for (int i = 0; i < tab; i++) {
			print("\t");
		}

		return this;
	}

	private String citar(String valor) {
		return "\"" + valor + "\"";
	}

	private XMLUtil print(String s) {
		pw.print(s);
		return this;
	}

	public XMLUtil ql() {
		return print(Constantes.QL);
	}

	public void close() {
		pw.close();
	}
}