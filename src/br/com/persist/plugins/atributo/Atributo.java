package br.com.persist.plugins.atributo;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.geradores.Variavel;

public class Atributo {
	private String viewToBack;
	private String parseDate;
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

	public String getParseDate() {
		if (Util.isEmpty(parseDate)) {
			parseDate = "false";
		}
		return parseDate;
	}

	public Boolean getParseDateBoolean() {
		return Boolean.valueOf(parseDate);
	}

	public void setParseDate(String parseDate) {
		this.parseDate = parseDate;
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

	public Variavel criarVariavel() {
		return new Variavel(classe, nome);
	}

	public Mapa criarMapa() {
		Mapa mapa = new Mapa();
		mapa.put("nome", nome);
		mapa.put("rotulo", rotulo);
		mapa.put("classe", classe);
		mapa.put("viewToBack", viewToBack);
		mapa.put("parseDate", getParseDate());
		mapa.setSemFormatacao(true);
		return mapa;
	}

	public void aplicar(Mapa mapa) {
		viewToBack = mapa.getString("viewToBack");
		parseDate = mapa.getString("parseDate");
		rotulo = mapa.getString("rotulo");
		classe = mapa.getString("classe");
		nome = mapa.getString("nome");
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