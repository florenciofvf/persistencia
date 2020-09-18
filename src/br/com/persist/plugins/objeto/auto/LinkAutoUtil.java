package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.Objeto;

public class LinkAutoUtil {

	private LinkAutoUtil() {
	}

	public static List<GrupoLinkAuto> listaGrupoLinkAuto(Objeto objeto, String string) {
		Map<String, GrupoLinkAuto> mapa = new LinkedHashMap<>();

		if (!Util.estaVazio(string)) {
			String[] links = string.split(";");

			if (links != null) {
				for (String link : links) {
					processarGrupoLinkAuto(objeto, link, mapa);
				}
			}
		}

		return new ArrayList<>(mapa.values());
	}

	private static void processarGrupoLinkAuto(Objeto objeto, String stringLink, Map<String, GrupoLinkAuto> mapa) {
		String[] campoTabelas = stringLink.split("=");

		if (campoTabelas != null && campoTabelas.length > 1) {
			String campo = campoTabelas[0].trim();

			GrupoLinkAuto grupo = mapa.computeIfAbsent(campo, GrupoLinkAuto::criar);

			String tabelas = campoTabelas[1];
			String[] arrayTabelas = tabelas.split(",");

			for (String apelidoTabelaCampo : arrayTabelas) {
				grupo.add(new TabelaLinkAuto(apelidoTabelaCampo.trim(), objeto.getId() + " > " + campo));
			}
		}
	}
}