package br.com.persist.plugins.check.objet;

import java.util.Map;

import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;
import br.com.persist.plugins.check.Procedimentos;

public class Field extends Procedimento {

	@Override
	public void processar(Map<String, Object> map, PilhaResultParam pilha) {
		empilharParametros(pilha);
		String string = pilha.popString();
		pilha.push(map.get(string));
	}

	@Override
	public Procedimento clonar() {
		Field resp = new Field();
		Procedimentos.clonarParametros(this, resp);
		return resp;
	}
}