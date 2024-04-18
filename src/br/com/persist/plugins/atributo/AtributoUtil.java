package br.com.persist.plugins.atributo;

import java.util.List;

public class AtributoUtil {
	private AtributoUtil() {
	}

	public static String getComponente(Mapa mapa) {
		return mapa.getString(AtributoConstantes.COMPONENTE);
	}

	public static String getBuscarTodos(Mapa mapa) {
		return mapa.getString(AtributoConstantes.BUSCAR_TODOS);
	}

	public static String getPesquisar(Mapa mapa) {
		return mapa.getString(AtributoConstantes.PESQUISAR);
	}

	public static String getExportar(Mapa mapa) {
		return mapa.getString(AtributoConstantes.EXPORTAR);
	}

	public static String getDetalhar(Mapa mapa) {
		return mapa.getString(AtributoConstantes.DETALHAR);
	}

	public static String getPesquisarFilter(Mapa mapa) {
		return AtributoUtil.getPesquisar(mapa) + "(filter)";
	}

	public static boolean contemParseDateValido(List<Atributo> atributos) {
		if (atributos == null) {
			return false;
		}
		for (Atributo att : atributos) {
			if (Boolean.TRUE.equals(att.getParseDateBoolean())) {
				return true;
			}
		}
		return false;
	}
}