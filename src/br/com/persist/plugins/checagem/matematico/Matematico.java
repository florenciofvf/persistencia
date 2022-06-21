package br.com.persist.plugins.checagem.matematico;

import br.com.persist.plugins.checagem.ChecagemNumero;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public abstract class Matematico extends FuncaoBinaria implements ChecagemNumero {
	public Object concatenar(Object pri, Object seg) {
		if (pri != null && seg == null) {
			return pri.toString() + "null";
		} else if (pri == null && seg != null) {
			return "null" + seg.toString();
		}
		return "nullnull";
	}
}