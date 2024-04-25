package br.com.persist.plugins.atributo;

import br.com.persist.assistencia.Constantes;

public class Separador extends Mapa {
	private final String string;

	public Separador(String string) {
		this.string = string == null ? Constantes.QL2 : string;
	}

	public Separador() {
		this(null);
	}

	public void processar(StringBuilder sb) {
		sb.append(string);
	}
}