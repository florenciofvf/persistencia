package br.com.persist.busca_apos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.util.Util;

public class BuscaAutoApos {

	private BuscaAutoApos() {
	}

	public static List<GrupoBuscaAutoApos> listaGrupoBuscaAutoApos(String string) {
		Map<String, GrupoBuscaAutoApos> mapa = new LinkedHashMap<>();

		if (!Util.estaVazio(string)) {
			String[] grupos = string.split(";");

			if (grupos != null) {
				for (String grupo : grupos) {
					processarGrupoBuscaAutoApos(grupo, mapa);
				}
			}
		}

		return new ArrayList<>(mapa.values());
	}

	private static void processarGrupoBuscaAutoApos(String stringGrupo, Map<String, GrupoBuscaAutoApos> mapa) {
		String[] grupoCampoTabelasDoGrupo = stringGrupo.split("=");

		if (grupoCampoTabelasDoGrupo != null && grupoCampoTabelasDoGrupo.length > 1) {
			String grupoCampo = grupoCampoTabelasDoGrupo[0].trim();

			GrupoBuscaAutoApos grupoBuscaAutoApos = mapa.get(grupoCampo);

			if (grupoBuscaAutoApos == null) {
				grupoBuscaAutoApos = new GrupoBuscaAutoApos(grupoCampo);
				mapa.put(grupoCampo, grupoBuscaAutoApos);
			} else {
				grupoBuscaAutoApos.getTabelas().clear();
			}

			String tabelasDoGrupo = grupoCampoTabelasDoGrupo[1];
			String[] tabelas = tabelasDoGrupo.split(",");

			for (String apelidoTabela : tabelas) {
				grupoBuscaAutoApos.getTabelas().add(new TabelaBuscaAutoApos(apelidoTabela.trim()));
			}
		}
	}
}