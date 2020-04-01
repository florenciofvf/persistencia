package br.com.persist.link_auto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.desktop.Objeto;
import br.com.persist.util.Util;

public class LinkAuto {

	private LinkAuto() {
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
		String[] campoTabelasDoLink = stringLink.split("=");

		if (campoTabelasDoLink != null && campoTabelasDoLink.length > 1) {
			String campo = campoTabelasDoLink[0].trim();

			GrupoLinkAuto grupoLinkAuto = mapa.get(campo);

			if (grupoLinkAuto == null) {
				grupoLinkAuto = new GrupoLinkAuto(campo);
				mapa.put(campo, grupoLinkAuto);
			} else {
				grupoLinkAuto.getTabelas().clear();
			}

			String tabelasDoLink = campoTabelasDoLink[1];
			String[] tabelas = tabelasDoLink.split(",");

			for (String apelidoTabelaCampo : tabelas) {
				grupoLinkAuto.getTabelas()
						.add(new TabelaLinkAuto(apelidoTabelaCampo.trim(), objeto.getId() + " > " + campo));
			}
		}
	}
}