package br.com.persist.util;

import org.xml.sax.Attributes;

import br.com.persist.objeto.FormularioInterno;

public class Form {
	private String objeto;
	private int largura;
	private int altura;
	private int x;
	private int y;

	public void copiar(FormularioInterno interno) {
		objeto = interno.getPainelObjeto().getObjeto().getId();
		largura = interno.getWidth();
		altura = interno.getHeight();
		x = interno.getX();
		y = interno.getY();
	}

	public void aplicar(Attributes attr) {
		largura = Integer.parseInt(attr.getValue("largura"));
		altura = Integer.parseInt(attr.getValue("altura"));
		x = Integer.parseInt(attr.getValue("x"));
		y = Integer.parseInt(attr.getValue("y"));
		objeto = attr.getValue("objeto");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("form");
		util.atributo("objeto", Util.escapar(objeto));
		util.atributo("largura", largura);
		util.atributo("altura", altura);
		util.atributo("x", x);
		util.atributo("y", y);
		util.fecharTag().finalizarTag("form");
	}

	public String getObjeto() {
		return objeto;
	}

	public int getLargura() {
		return largura;
	}

	public int getAltura() {
		return altura;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}