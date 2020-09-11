package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.util.Util;

public class BuscaAutoUtil {

	private BuscaAutoUtil() {
	}

	public static List<GrupoBuscaAuto> listaGrupoBuscaAuto(Objeto objeto, String string) {
		Map<String, GrupoBuscaAuto> mapa = new LinkedHashMap<>();

		if (!Util.estaVazio(string)) {
			String[] grupos = string.split(";");

			if (grupos != null) {
				for (String grupo : grupos) {
					processarGrupoBuscaAuto(objeto, grupo, mapa);
				}
			}
		}

		return new ArrayList<>(mapa.values());
	}

	private static void processarGrupoBuscaAuto(Objeto objeto, String stringGrupo, Map<String, GrupoBuscaAuto> mapa) {
		String[] grupoCampoTabelas = stringGrupo.split("=");

		if (grupoCampoTabelas != null && grupoCampoTabelas.length > 1) {
			String grupoCampo = grupoCampoTabelas[0].trim();

			GrupoBuscaAuto grupo = mapa.computeIfAbsent(grupoCampo, GrupoBuscaAuto::criar);
			GrupoBuscaAutoApos grupoApos = grupo.getGrupoBuscaAutoApos();

			String tabelas = grupoCampoTabelas[1];
			String[] arrayTabelas = tabelas.split(",");

			for (String apelidoTabelaCampo : arrayTabelas) {
				TabelaBuscaAuto tabela = new TabelaBuscaAuto(apelidoTabelaCampo.trim(),
						objeto.getId() + " > " + grupoCampo);
				extrairTabelaApos(grupoApos, tabela.getTabelasApos());
				grupo.add(tabela);
			}
		}
	}

	private static void extrairTabelaApos(GrupoBuscaAutoApos grupo, String tabelasApos) {
		if (Util.estaVazio(tabelasApos)) {
			return;
		}

		String[] arrayTabelaApos = tabelasApos.split("-");

		for (String tabelaApos : arrayTabelaApos) {
			String[] arrayApelidoTabela = TabelaBuscaAuto.separarApelidoTabela(tabelaApos);
			grupo.add(new TabelaBuscaAutoApos(arrayApelidoTabela[0], arrayApelidoTabela[1]));
		}
	}
}