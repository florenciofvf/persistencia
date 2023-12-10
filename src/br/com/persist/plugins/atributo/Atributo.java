package br.com.persist.plugins.atributo;

import java.util.List;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;
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

	public static String gerarParamJS(List<Atributo> atributos) {
		StringBuilder sb = new StringBuilder();
		sb.append("\tfunction criarParam() {" + Constantes.QL);
		sb.append(criarParamJS(atributos) + Constantes.QL);
		sb.append("\t\treturn param;" + Constantes.QL);
		sb.append("\t}" + Constantes.QL);
		return sb.toString();
	}

	private static String criarParamJS(List<Atributo> atributos) {
		StringBuilder sb = new StringBuilder("\t\tvar param = {" + Constantes.QL);
		for (int i = 0; i < atributos.size(); i++) {
			Atributo att = atributos.get(i);
			sb.append(att.gerarDeclaracaoJS());
			if (i + 1 < atributos.size()) {
				sb.append(",");
			}
			sb.append(Constantes.QL);
		}
		sb.append("\t\t};" + Constantes.QL);
		return sb.toString();
	}

	private String gerarDeclaracaoJS() {
		return "\t\t\t" + nome + ": vm.filtro." + nome;
	}

	public String gerarObrigatorioJS() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\tif(" + gerarIsVazioJS() + ") {" + Constantes.QL);
		sb.append("\t\t\treturn 'Campo " + rotulo + " Obrigat\u00F3rio.';" + Constantes.QL);
		sb.append("\t\t}" + Constantes.QL);
		return sb.toString();
	}

	public String gerarIsVazioJS() {
		return "isVazio(vm.filtro." + nome + ")";
	}

	@Override
	public String toString() {
		return nome;
	}
}