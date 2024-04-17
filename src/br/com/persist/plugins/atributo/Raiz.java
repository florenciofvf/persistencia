package br.com.persist.plugins.atributo;

import br.com.persist.assistencia.Util;
import br.com.persist.geradores.Variavel;

public class Raiz {
	private final Mapa mapa;

	public Raiz(Mapa mapa) {
		this.mapa = mapa == null ? new Mapa() : mapa;
	}

	public Raiz() {
		this(new Mapa());
	}

	public Mapa getMapa() {
		return mapa;
	}

	public Mapa getMapaAtributos() {
		return mapa.getMapa(AtributoConstantes.ATRIBUTOS);
	}

	public String getFiltroJSCapitalize() {
		return Util.capitalize(getFiltroJS());
	}

	public String getFiltroJS() {
		return mapa.getString(AtributoConstantes.FILTRO_JS);
	}

	public Mapa getMapaControllerJS() {
		return mapa.getMapa(AtributoConstantes.CONTROLLER_JS);
	}

	public Mapa getMapaServiceJS() {
		return mapa.getMapa(AtributoConstantes.SERVICE_JS);
	}

	public String getDTO() {
		return mapa.getString(AtributoConstantes.DTO);
	}

	public String getListDTO() {
		return "List<" + getDTO() + ">";
	}

	public String getFilter() {
		return mapa.getString(AtributoConstantes.FILTER);
	}

	public Variavel getTipoFilter() {
		return new Variavel(getFilter(), "filter");
	}

	public Mapa getMapaRest() {
		return mapa.getMapa(AtributoConstantes.REST);
	}

	public Mapa getMapaService() {
		return mapa.getMapa(AtributoConstantes.SERVICE);
	}

	public String getBean() {
		return mapa.getString(AtributoConstantes.BEAN);
	}

	public Mapa getMapaDAO() {
		return mapa.getMapa(AtributoConstantes.DAO);
	}

	public Variavel getTipoDAO() {
		Mapa mapaDAO = getMapaDAO();
		if (mapaDAO == null) {
			return null;
		}
		return new Variavel(AtributoUtil.getComponente(mapaDAO), "dao");
	}

	public String getDAOImpl() {
		return mapa.getString(AtributoConstantes.DAO_IMPL);
	}

	public Mapa getMapaTest() {
		return mapa.getMapa(AtributoConstantes.TEST);
	}
}