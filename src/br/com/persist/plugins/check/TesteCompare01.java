package br.com.persist.plugins.check;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TesteCompare01 {
	private static final Logger LOG = Logger.getGlobal();

	public static void main(String[] args) {
		Check.selecionar(TesteCompare01.class.getSimpleName() + ".xml");
		List<Object> lista = Check.check(criarMap());
		for (Object obj : lista) {
			LOG.log(Level.INFO, "{0}", obj);
		}
	}

	private static Map<String, Object> criarMap() {
		Map<String, Object> resp = new HashMap<>();
		resp.put("nome", "Maria da Silva");
		return resp;
	}
}