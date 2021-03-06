package br.com.persist.plugins.check.proc;

import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;
import br.com.persist.plugins.check.Procedimentos;

public class Logico {
	private Logico() {
	}

	public static class And extends Procedimento {
		@Override
		public void processar(Map<String, Object> map, PilhaResultParam pilha) {
			String string = null;
			if (getTotalParametros() == 1) {
				string = getParametroString(0);
			}
			Boolean param1 = pilha.popBoolean();
			Boolean param2 = pilha.popBoolean();
			Boolean boolea = param1 && param2;
			if (boolea && !Util.estaVazio(string)) {
				pilha.push(string);
			} else {
				pilha.push(boolea);
			}
		}

		@Override
		public Procedimento clonar() {
			And resp = new And();
			Procedimentos.clonarParametros(this, resp);
			return resp;
		}
	}

	public static class Or extends Procedimento {
		@Override
		public void processar(Map<String, Object> map, PilhaResultParam pilha) {
			String string = null;
			if (getTotalParametros() == 1) {
				string = getParametroString(0);
			}
			Boolean param1 = pilha.popBoolean();
			Boolean param2 = pilha.popBoolean();
			Boolean boolea = param1 || param2;
			if (boolea && !Util.estaVazio(string)) {
				pilha.push(string);
			} else {
				pilha.push(boolea);
			}
		}

		@Override
		public Procedimento clonar() {
			Or resp = new Or();
			Procedimentos.clonarParametros(this, resp);
			return resp;
		}
	}

	public static class Xor extends Procedimento {
		@Override
		public void processar(Map<String, Object> map, PilhaResultParam pilha) {
			String string = null;
			if (getTotalParametros() == 1) {
				string = getParametroString(0);
			}
			Boolean param1 = pilha.popBoolean();
			Boolean param2 = pilha.popBoolean();
			Boolean boolea = param1 ^ param2;
			if (boolea && !Util.estaVazio(string)) {
				pilha.push(string);
			} else {
				pilha.push(boolea);
			}
		}

		@Override
		public Procedimento clonar() {
			Xor resp = new Xor();
			Procedimentos.clonarParametros(this, resp);
			return resp;
		}
	}
}