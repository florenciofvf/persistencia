package br.com.persist.plugins.atributo;

import br.com.persist.plugins.atributo.aux.Tipo;

public class AtributoUtil {
	private AtributoUtil() {
	}

	public static Tipo getTipoDAO(Mapa mapa) {
		return new Tipo(getComponente(mapa), "dao");
	}

	public static String getComponente(Mapa mapa) {
		return mapa.getString(AtributoConstantes.COMPONENTE);
	}

	public static String getPesquisar(Mapa mapa) {
		return mapa.getString(AtributoConstantes.PESQUISAR);
	}

	public static String getPesquisarFilter(Mapa mapa) {
		return AtributoUtil.getPesquisar(mapa) + "(filter)";
	}

	public static String getExportar(Mapa mapa) {
		return mapa.getString(AtributoConstantes.EXPORTAR);
	}
}