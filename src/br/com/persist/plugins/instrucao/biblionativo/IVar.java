package br.com.persist.plugins.instrucao.biblionativo;

import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class IVar {
	private IVar() {
	}

	@Biblio
	public static String valor(Object nome) {
		if (nome == null) {
			return "";
		}
		Variavel variavel = VariavelProvedor.getVariavel(nome.toString());
		if (variavel != null) {
			return variavel.getValor();
		}
		return "";
	}
}