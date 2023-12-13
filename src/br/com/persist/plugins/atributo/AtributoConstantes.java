package br.com.persist.plugins.atributo;

import br.com.persist.plugins.atributo.aux.Import;
import br.com.persist.plugins.atributo.aux.Tipo;

public class AtributoConstantes {
	public static final String APPLICATION_JSON = "{MediaType.APPLICATION_JSON}";
	public static final Tipo TIPO_SERVICE = new Tipo("Service", "service");
	public static final Import IMPORT_LIST = new Import("java.util.List");
	public static final Tipo TIPO_FILTER = new Tipo("Filter", "filter");

	public static final String PESQUISAR = "pesquisar";
	public static final String FUNCTION = "function ";
	public static final String FILTRO = "filtro";
	public static final String PUBLIC = "public";

	public static final String LABEL_ATRIBUTO_MIN = "label.atributo_min";
	public static final String PAINEL_ATRIBUTO = "PAINEL ATRIBUTO";
	public static final String LABEL_ATRIBUTO = "label.atributo";
	public static final String IGNORADOS = "ignorados";
	public static final String ATRIBUTO = "atributo";

	private AtributoConstantes() {
	}
}