package br.com.persist.plugins.check.conver;

import java.util.Map;

import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;
import br.com.persist.plugins.check.Procedimentos;

public class ParseString extends Procedimento {

	@Override
	public void processar(Map<String, Object> map, PilhaResultParam pilha) {
		empilharParametros(pilha);
		pilha.push(pilha.popString());
	}

	@Override
	public Procedimento clonar() {
		ParseString resp = new ParseString();
		Procedimentos.clonarParametros(this, resp);
		return resp;
	}
}