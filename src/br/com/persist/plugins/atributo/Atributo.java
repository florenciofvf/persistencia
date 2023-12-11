package br.com.persist.plugins.atributo;

import org.xml.sax.Attributes;

import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.atributo.aux.Tipo;

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

	public Tipo criarTipo() {
		return new Tipo(classe, nome);
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(AtributoConstantes.ATRIBUTO);
		util.atributo("nome", nome);
		util.atributo("rotulo", rotulo);
		util.atributo("classe", classe);
		util.fecharTag(-1);
	}

	public void aplicar(Attributes attr) {
		rotulo = attr.getValue("rotulo");
		classe = attr.getValue("classe");
		nome = attr.getValue("nome");
	}

	public String gerarIsVazioJS() {
		return "isVazio(vm.filtro." + nome + ")";
	}

	@Override
	public String toString() {
		return nome;
	}
}