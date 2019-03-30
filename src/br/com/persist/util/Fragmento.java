package br.com.persist.util;

import org.xml.sax.Attributes;

import br.com.persist.xml.XMLUtil;

public class Fragmento {
	private String resumo;
	private String grupo;
	private String valor;

	public Fragmento clonar() {
		Fragmento c = new Fragmento();

		c.resumo = resumo;
		c.grupo = grupo;
		c.valor = valor;

		return c;
	}

	public void aplicar(Attributes attr) {
		resumo = attr.getValue("resumo");
		grupo = attr.getValue("grupo");
		valor = attr.getValue("valor");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("fragmento");
		util.atributo("resumo", Util.escapar(resumo));
		util.atributo("grupo", Util.escapar(grupo));
		util.atributo("valor", Util.escapar(valor));
		util.fecharTag().finalizarTag("fragmento");
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getValor() {
		return valor;
	}

	public boolean isValida() {
		return !Util.estaVazio(resumo) && !Util.estaVazio(grupo);
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return resumo;
	}
}