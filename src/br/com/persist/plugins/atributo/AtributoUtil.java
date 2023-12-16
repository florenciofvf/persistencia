package br.com.persist.plugins.atributo;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.atributo.aux.Tipo;

public class AtributoUtil {
	private AtributoUtil() {
	}

	public static String getFiltroCapitalize(Mapa mapa) {
		return Util.capitalize(getFiltro(mapa));
	}

	public static Mapa getMapaAtributos(Mapa mapa) {
		return mapa.getMapa(AtributoConstantes.ATRIBUTOS);
	}

	public static String getFiltro(Mapa mapa) {
		return mapa.getString(AtributoConstantes.FILTRO);
	}

	public static Mapa getMapaControllerJS(Mapa mapa) {
		return mapa.getMapa(AtributoConstantes.CONTROLLER_JS);
	}

	public static Mapa getMapaServiceJS(Mapa mapa) {
		return mapa.getMapa(AtributoConstantes.SERVICE_JS);
	}

	public static String getDTO(Mapa mapa) {
		return mapa.getString(AtributoConstantes.DTO);
	}

	public static String getListDTO(Mapa mapa) {
		return "List<" + getDTO(mapa) + ">";
	}

	public static String getFilter(Mapa mapa) {
		return mapa.getString(AtributoConstantes.FILTER);
	}

	public static Tipo getTipoFilter(Mapa mapa) {
		return new Tipo(getFilter(mapa), "filter");
	}

	public static Mapa getMapaRest(Mapa mapa) {
		return mapa.getMapa(AtributoConstantes.REST);
	}

	public static Mapa getMapaService(Mapa mapa) {
		return mapa.getMapa(AtributoConstantes.SERVICE);
	}

	public static String getBean(Mapa mapa) {
		return mapa.getString(AtributoConstantes.BEAN);
	}

	public static Mapa getMapaDAO(Mapa mapa) {
		return mapa.getMapa(AtributoConstantes.DAO);
	}

	public static Tipo getTipoDAO(Mapa mapa) {
		return new Tipo(getComponente(mapa), "dao");
	}

	public static String getDAOImpl(Mapa mapa) {
		return mapa.getString(AtributoConstantes.DAO_IMPL);
	}

	public static Mapa getMapaTest(Mapa mapa) {
		return mapa.getMapa(AtributoConstantes.TEST);
	}

	public static String getComponente(Mapa mapa) {
		return mapa.getString(AtributoConstantes.COMPONENTE);
	}

	public static String getPesquisar(Mapa mapa) {
		return mapa.getString(AtributoConstantes.PESQUISAR);
	}

	public static String getExportar(Mapa mapa) {
		return mapa.getString(AtributoConstantes.EXPORTAR);
	}

	public static String pesquisarFilter(Mapa mapa) {
		return AtributoUtil.getPesquisar(mapa) + "(filter)";
	}
}