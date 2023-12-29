package br.com.persist.plugins.atributo;

public class AtributoUtil {
	private AtributoUtil() {
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

	public static String getPesquisarFilter(Mapa mapa) {
		return AtributoUtil.getPesquisar(mapa) + "(filter)";
	}
}