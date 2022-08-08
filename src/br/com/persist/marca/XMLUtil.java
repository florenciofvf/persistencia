package br.com.persist.marca;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class XMLUtil {
	private final PrintWriter pw;
	private int tab = -1;

	public XMLUtil(Writer out) throws XMLException {
		try {
			pw = new PrintWriter(out);
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public XMLUtil(File file) throws XMLException {
		try {
			pw = new PrintWriter(file, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public XMLUtil prologo() {
		return print("<?xml").atributo("version", 1.0).atributo("encoding", StandardCharsets.UTF_8.name()).print("?>")
				.ql().ql();
	}

	public XMLUtil atributoCheck(String nome, boolean valor) {
		return valor ? print(" " + nome + "=" + citar(Constantes.VAZIO + valor)) : this;
	}

	public XMLUtil atributo(String nome, boolean valor) {
		return print(" " + nome + "=" + citar(Constantes.VAZIO + valor));
	}

	public XMLUtil atributo(String nome, double valor) {
		return print(" " + nome + "=" + citar(Constantes.VAZIO + valor));
	}

	public XMLUtil atributo(String nome, long valor) {
		return print(" " + nome + "=" + citar(Constantes.VAZIO + valor));
	}

	public XMLUtil atributoCheck(String nome, String valor) {
		return Util.estaVazio(valor) ? this : print(" " + nome + "=" + citar(valor));
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

	public XMLUtil fecharTag2(int deltaTab) {
		print("/>").ql();
		tab += deltaTab;
		return this;
	}

	public XMLUtil abrirTag2(String nome) {
		return abrirTag(nome).fecharTag();
	}

	public XMLUtil finalizarTag(String nome) {
		tabular().print("</" + nome + ">").ql();
		tab--;
		return this;
	}

	private XMLUtil tabular() {
		for (int i = 0; i < tab; i++) {
			print("\t");
		}
		return this;
	}

	public static String citar(String valor) {
		return "\"" + Util.escapar(valor) + "\"";
	}

	public XMLUtil print(String s) {
		pw.print(s);
		return this;
	}

	public XMLUtil tab() {
		return print(Constantes.TAB);
	}

	public XMLUtil ql() {
		return print(Constantes.QL);
	}

	public void close() {
		pw.close();
	}
}