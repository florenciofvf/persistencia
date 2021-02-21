package br.com.persist.plugins.check;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.check.compar.Igual;
import br.com.persist.plugins.check.conver.ParseBoolean;
import br.com.persist.plugins.check.conver.ParseInt;
import br.com.persist.plugins.check.logico.And;
import br.com.persist.plugins.check.logico.Or;
import br.com.persist.plugins.check.logico.Xor;
import br.com.persist.plugins.check.matema.Somar;
import br.com.persist.plugins.check.objet.Field;

public final class Procedimentos {
	private static final Map<String, Procedimento> map = new HashMap<>();

	private Procedimentos() {
	}

	static {
		map.put("parseBoolean", new ParseBoolean());
		map.put("parseInt", new ParseInt());
		map.put("somar", new Somar());
		map.put("igual", new Igual());
		map.put("field", new Field());
		map.put("and", new And());
		map.put("xor", new Xor());
		map.put("or", new Or());
	}

	public static Procedimento get(String nome) {
		return map.get(nome).clonar();
	}

	public static void clonarParametros(Procedimento origem, Procedimento destino) {
		for (Object obj : origem.getParametros()) {
			destino.addParam(obj);
		}
	}
}