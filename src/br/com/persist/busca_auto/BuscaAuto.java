package br.com.persist.busca_auto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.util.Util;

public class BuscaAuto {

	private BuscaAuto() {
	}

	public static List<GrupoBuscaAuto> listaGrupoBuscaAuto(String string) {
		Map<String, GrupoBuscaAuto> mapa = new LinkedHashMap<>();

		if (!Util.estaVazio(string)) {
			String[] grupos = string.split(";");

			if (grupos != null) {
				for (String grupo : grupos) {
					processarGrupoBuscaAuto(grupo, mapa);
				}
			}
		}

		return new ArrayList<>(mapa.values());
	}

	private static void processarGrupoBuscaAuto(String stringGrupo, Map<String, GrupoBuscaAuto> mapa) {
		String[] grupoCampoTabelasDoGrupo = stringGrupo.split("=");

		if (grupoCampoTabelasDoGrupo != null && grupoCampoTabelasDoGrupo.length > 1) {
			String grupoCampo = grupoCampoTabelasDoGrupo[0].trim();

			GrupoBuscaAuto grupoBuscaAuto = mapa.get(grupoCampo);

			if (grupoBuscaAuto == null) {
				grupoBuscaAuto = new GrupoBuscaAuto(grupoCampo);
				mapa.put(grupoCampo, grupoBuscaAuto);
			} else {
				grupoBuscaAuto.getTabelas().clear();
			}

			String tabelasDoGrupo = grupoCampoTabelasDoGrupo[1];
			String[] tabelas = tabelasDoGrupo.split(",");

			for (String apelidoTabelaCampo : tabelas) {
				grupoBuscaAuto.getTabelas().add(new TabelaBuscaAuto(apelidoTabelaCampo.trim()));
			}
		}
	}
}