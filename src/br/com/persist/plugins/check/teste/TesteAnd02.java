package br.com.persist.plugins.check.teste;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.plugins.check.Check;

public class TesteAnd02 {
	private static final Logger LOG = Logger.getGlobal();

	public static void main(String[] args) {
		Check.selecionar(TesteAnd02.class.getSimpleName() + ".xml");
		List<Object> lista = Check.check(criarMap());
		for (Object obj : lista) {
			LOG.log(Level.INFO, "{0}", obj);
		}
	}

	private static Map<String, Object> criarMap() {
		Map<String, Object> resp = new HashMap<>();
		resp.put("TP_STATUS", "G");
		resp.put("ST_TRANSACAO", "S");
		return resp;
	}
}