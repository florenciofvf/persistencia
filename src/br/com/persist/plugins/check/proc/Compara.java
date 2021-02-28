package br.com.persist.plugins.check.proc;

import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;
import br.com.persist.plugins.check.Procedimentos;

public class Compara {
	private Compara() {
	}

	public static class Igual extends Procedimento {
		@Override
		public void processar(Map<String, Object> map, PilhaResultParam pilha) {
			String string = null;
			if (getTotalParametros() == 1) {
				string = getParametroString(0);
			}
			Object param1 = pilha.pop();
			Object param2 = pilha.pop();
			Boolean boolea = param1.equals(param2);
			if (boolea && !Util.estaVazio(string)) {
				pilha.push(string);
			} else {
				pilha.push(boolea);
			}
		}

		@Override
		public Procedimento clonar() {
			Igual resp = new Igual();
			Procedimentos.clonarParametros(this, resp);
			return resp;
		}
	}
}