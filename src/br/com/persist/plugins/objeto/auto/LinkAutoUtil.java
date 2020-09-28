package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Constantes;
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
				grupo.add(criar(apelidoTabelaCampo.trim(), objeto.getId() + " > " + campo));
			}
		}
	}

	private static TabelaLinkAuto criar(String apelidoTabelaCampo, String contextoDebug) {
		int pos = apelidoTabelaCampo.indexOf('.');
		checarPos(pos, "SEM CAMPO DEFINIDO NO LINK AUTO -> " + contextoDebug + " > " + apelidoTabelaCampo);
		String[] arrayApelidoTabela = separarApelidoTabela(apelidoTabelaCampo.substring(0, pos));
		String campo = apelidoTabelaCampo.substring(pos + 1).trim();
		String apelido = arrayApelidoTabela[0];
		String nome = arrayApelidoTabela[1];
		checarCampo(campo);
		return new TabelaLinkAuto(apelido, nome, campo);
	}

	private static void checarCampo(String campo) {
		if (Util.estaVazio(campo)) {
			throw new IllegalStateException("Nome do campo vazio");
		}
	}

	private static void checarPos(int pos, String msg) {
		if (pos < 0) {
			throw new IllegalStateException(msg);
		}
	}

	private static String[] separarApelidoTabela(String string) {
		String[] strings = new String[2];
		if (Util.estaVazio(string)) {
			throw new IllegalArgumentException("separarApelidoTabela");
		}
		string = string.trim();
		if (string.startsWith("(")) {
			int pos2 = string.indexOf(')');
			if (pos2 == -1) {
				throw new IllegalArgumentException("separarApelidoTabela falta fechar com -> )");
			}
			strings[0] = string.substring(1, pos2).trim();
			strings[1] = string.substring(pos2 + 1).trim();
		} else {
			strings[0] = Constantes.VAZIO;
			strings[1] = string;
		}
		if (Util.estaVazio(strings[1])) {
			throw new IllegalStateException("Nome da tabela vazio");
		}
		return strings;
	}
}