package br.com.persist.plugins.check;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.check.matema.Somar;
import br.com.persist.plugins.check.objet.Field;
import br.com.persist.plugins.check.proc.Compara;
import br.com.persist.plugins.check.proc.Converte;
import br.com.persist.plugins.check.proc.Logico;

public final class Procedimentos {
	private static final Map<String, Procedimento> map = new HashMap<>();

	private Procedimentos() {
	}

	static {
		map.put("parseBoolean", new Converte.ParseBoolean());
		map.put("bool", new Converte.ParseBoolean());

		map.put("parseString", new Converte.ParseString());
		map.put("string", new Converte.ParseString());

		map.put("parseInt", new Converte.ParseInt());
		map.put("int", new Converte.ParseInt());

		map.put("somar", new Somar());

		map.put("igual", new Compara.Igual());
		map.put("eq", new Compara.Igual());

		map.put("field", new Field());

		map.put("and", new Logico.And());
		map.put("xor", new Logico.Xor());
		map.put("or", new Logico.Or());
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