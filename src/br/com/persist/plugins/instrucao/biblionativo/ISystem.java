package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ISystem {
	private ISystem() {
	}

	@Biblio(0)
	public static BigInteger timeMillis() {
		return BigInteger.valueOf(System.currentTimeMillis());
	}

	@Biblio(1)
	public static BigInteger nanoTime() {
		return BigInteger.valueOf(System.nanoTime());
	}

	@Biblio(2)
	public static Lista properties() {
		Lista lista = new Lista();
		Properties properties = System.getProperties();
		Set<String> nomes = properties.stringPropertyNames();
		for (String item : nomes) {
			Object object = properties.get(item);
			String valor = object == null ? "" : object.toString();
			Map<String, String> map = new HashMap<>();
			map.put(item, valor);
			lista.add(map);
		}
		return lista;
	}

	@Biblio(3)
	public static Lista environment() {
		Lista lista = new Lista();
		Map<String, String> map = System.getenv();
		for (Map.Entry<String, String> item : map.entrySet()) {
			String value = item.getValue();
			String valor = value == null ? "" : value;
			Map<String, String> mapa = new HashMap<>();
			mapa.put(item.getKey(), valor);
			lista.add(mapa);
		}
		return lista;
	}
}