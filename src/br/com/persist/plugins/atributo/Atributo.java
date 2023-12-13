package br.com.persist.plugins.atributo;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.atributo.aux.Tipo;

public class Atributo {
	private String viewToBack;
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

	public String getViewToBack() {
		return viewToBack;
	}

	public void setViewToBack(String viewToBack) {
		this.viewToBack = viewToBack;
	}

	public Tipo criarTipo() {
		return new Tipo(classe, nome);
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(AtributoConstantes.ATRIBUTO);
		util.atributo("nome", nome);
		util.atributoCheck("rotulo", rotulo);
		util.atributoCheck("classe", classe);
		util.atributoCheck("viewToBack", viewToBack);
		util.fecharTag(-1);
	}

	public void aplicar(Attributes attr) {
		viewToBack = attr.getValue("viewToBack");
		rotulo = attr.getValue("rotulo");
		classe = attr.getValue("classe");
		nome = attr.getValue("nome");
	}

	public String gerarIsVazioJS(String filtro) {
		return "isVazio(vm." + filtro + "." + nome + ")";
	}

	public String gerarViewToBack(String filtro) {
		final String string = "vm." + filtro + "." + nome;
		if (Util.isEmpty(viewToBack)) {
			return string;
		}
		return Util.replaceAll(viewToBack, Constantes.SEP + "valor" + Constantes.SEP, string);
	}

	@Override
	public String toString() {
		return nome;
	}
}