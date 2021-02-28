package br.com.persist.plugins.check.proc;

import java.util.Map;

import br.com.persist.plugins.check.PilhaResultParam;
import br.com.persist.plugins.check.Procedimento;
import br.com.persist.plugins.check.Procedimentos;

public class Converte {
	private Converte() {
	}

	public static class ParseBoolean extends Procedimento {
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

	public static class ParseDouble extends Procedimento {
		@Override
		public void processar(Map<String, Object> map, PilhaResultParam pilha) {
			empilharParametros(pilha);
			String string = pilha.popString();
			pilha.push(Double.valueOf(string));
		}

		@Override
		public Procedimento clonar() {
			ParseDouble resp = new ParseDouble();
			Procedimentos.clonarParametros(this, resp);
			return resp;
		}
	}

	public static class ParseInt extends Procedimento {
		@Override
		public void processar(Map<String, Object> map, PilhaResultParam pilha) {
			empilharParametros(pilha);
			String string = pilha.popString();
			pilha.push(Integer.valueOf(string));
		}

		@Override
		public Procedimento clonar() {
			ParseInt resp = new ParseInt();
			Procedimentos.clonarParametros(this, resp);
			return resp;
		}
	}

	public static class ParseString extends Procedimento {
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
}