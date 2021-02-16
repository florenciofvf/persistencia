package br.com.persist.plugins.check.compar;

import java.util.Map;

import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;
import br.com.persist.plugins.check.Procedimentos;

public class Igual extends Procedimento {

	@Override
	public void processar(Map<String, Object> map, PilhaResultParam pilha) {
		empilharParametros(pilha);
		Object param1 = pilha.pop();
		Object param2 = pilha.pop();
		pilha.push(param1.equals(param2));
	}

	@Override
	public Procedimento clonar() {
		Igual resp = new Igual();
		Procedimentos.clonarParametros(this, resp);
		return resp;
	}
}