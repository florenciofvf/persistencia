package br.com.persist.plugins.atributo;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.atributo.aux.Tipo;

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

	public String getFiltroCapitalize() {
		return Util.capitalize(getFiltro());
	}

	public String getFiltro() {
		return mapa.getString(AtributoConstantes.FILTRO);
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

	public Tipo getTipoFilter() {
		return new Tipo(getFilter(), "filter");
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

	public Tipo getTipoDAO() {
		Mapa mapaDAO = getMapaDAO();
		if (mapaDAO == null) {
			return null;
		}
		return new Tipo(AtributoUtil.getComponente(mapaDAO), "dao");
	}

	public String getDAOImpl() {
		return mapa.getString(AtributoConstantes.DAO_IMPL);
	}

	public Mapa getMapaTest() {
		return mapa.getMapa(AtributoConstantes.TEST);
	}
}