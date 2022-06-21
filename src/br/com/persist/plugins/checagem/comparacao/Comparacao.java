package br.com.persist.plugins.checagem.comparacao;

import br.com.persist.plugins.checagem.ChecagemNumero;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public abstract class Comparacao extends FuncaoBinaria implements ChecagemNumero {
	public boolean iguais(Object obj1, Object obj2) {
		return obj1 != null ? obj1.equals(obj2) : obj2 == null;
	}
}