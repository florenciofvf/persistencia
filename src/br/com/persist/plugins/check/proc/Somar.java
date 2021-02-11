package br.com.persist.plugins.check.proc;

import java.util.Map;

import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;

public class Somar extends Procedimento {

	@Override
	public void processar(Map<String, Object> map, PilhaResultParam pilha) {
		empilharParametros(pilha);
		Object param1 = pilha.pop();
		Object param2 = pilha.pop();
		Number number = null;
		if (isDouble(param1) || isDouble(param2)) {
			number = getDouble(param1) + getDouble(param2);
		} else {
			number = getInt(param1) + getInt(param2);
		}
		pilha.push(number);
	}

	@Override
	public Procedimento clonar() {
		Somar resp = new Somar();
		Procedimentos.clonarParametros(this, resp);
		return resp;
	}
}