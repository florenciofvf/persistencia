package br.com.persist.plugins.check.proc;

import java.util.Map;

import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;

public class ParseInt extends Procedimento {

	@Override
	public void processar(Map<String, Object> map, PilhaResultParam pilha) {
		empilharParametros(pilha);
		String string = pilha.pop().toString();
		pilha.push(Integer.valueOf(string));
	}

	@Override
	public Procedimento clonar() {
		ParseInt resp = new ParseInt();
		Procedimentos.clonarParametros(this, resp);
		return resp;
	}
}