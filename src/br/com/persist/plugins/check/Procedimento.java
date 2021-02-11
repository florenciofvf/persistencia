package br.com.persist.plugins.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Procedimento {
	protected List<Object> parametros;

	public Procedimento() {
		parametros = new ArrayList<>();
	}

	protected void empilharParametros(PilhaResultParam pilha) {
		for (Object obj : parametros) {
			pilha.push(obj);
		}
	}

	public abstract void processar(Map<String, Object> map, PilhaResultParam pilha);

	public abstract Procedimento clonar();
}