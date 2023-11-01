package br.com.persist.plugins.conexao;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.assistencia.Util;

public class ConexaoUtil {
	private ConexaoUtil() {
	}

	public static Map<String, String> criarMapaTiposFuncoes(String string) {
		Map<String, String> mapa = new HashMap<>();
		if (!Util.isEmpty(string)) {
			String[] strings = string.split(";");
			if (strings != null) {
				for (String chaveValor : strings) {
					String[] stringsCV = chaveValor.split("=");
					if (stringsCV != null && stringsCV.length > 1) {
						mapa.put(stringsCV[0].trim().toLowerCase(), stringsCV[1].trim());
					}
				}
			}
		}
		return mapa;
	}
}