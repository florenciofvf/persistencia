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

	public boolean isPesquisarRetornoLista() {
		return Boolean.parseBoolean(mapa.getString(AtributoConstantes.PESQUISAR_RETORNO_LISTA));
	}

	public Mapa getMapaAtributos() {
		return mapa.getMapa(AtributoConstantes.ATRIBUTOS);
	}

	public String getFilterJSCapitalize() {
		return Util.capitalize(getFilterJSPesquisarExportar());
	}

	public String getFilterJSPesquisarExportar() {
		return mapa.getString(AtributoConstantes.FILTER_JS);
	}

	public Mapa getMapaControllerJS() {
		return mapa.getMapa(AtributoConstantes.CONTROLLER_JS);
	}

	public Mapa getMapaServiceJS() {
		return mapa.getMapa(AtributoConstantes.SERVICE_JS);
	}

	public String getDTOPesquisa() {
		return mapa.getString(AtributoConstantes.DTO_PESQUISAR);
	}

	public String getDTODetalhe() {
		return mapa.getString(AtributoConstantes.DTO_DETALHAR);
	}

	public String getDTOTodos() {
		return mapa.getString(AtributoConstantes.DTO_TODOS);
	}

	public String getListDTOTodos() {
		return "List<" + getDTOTodos() + ">";
	}

	public String getListDTOPesquisa() {
		return "List<" + getDTOPesquisa() + ">";
	}

	public String getFilterJVPesquisarExportar() {
		return mapa.getString(AtributoConstantes.FILTER_JV);
	}

	public Variavel getTipoFilterJVPesquisarExportar() {
		return new Variavel(getFilterJVPesquisarExportar(), "filter");
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