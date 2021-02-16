package br.com.persist.plugins.check.conver;

import java.util.Map;

import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;
import br.com.persist.plugins.check.Procedimentos;

public class ParseBoolean extends Procedimento {

	@Override
	public void processar(Map<String, Object> map, PilhaResultParam pilha) {
		empilharParametros(pilha);
		String string = pilha.popString();
		pilha.push(Boolean.valueOf(string));
	}

	@Override
	public Procedimento clonar() {
		ParseBoolean resp = new ParseBoolean();
		Procedimentos.clonarParametros(this, resp);
		return resp;
	}
}