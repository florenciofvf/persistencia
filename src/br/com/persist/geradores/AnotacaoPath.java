package br.com.persist.geradores;

import br.com.persist.assistencia.Util;

public class AnotacaoPath extends Anotacao {
	protected AnotacaoPath(String string) {
		super("Path(" + Util.citar2("/" + string) + ")");
	}
}