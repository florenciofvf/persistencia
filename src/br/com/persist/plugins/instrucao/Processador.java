package br.com.persist.plugins.instrucao;

import java.util.HashMap;
import java.util.Map;

public class Processador {
	private final CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final PilhaOperando pilhaOperando = new PilhaOperando();
	private final PilhaMetodo pilhaMetodo = new PilhaMetodo();
	static final Map<String, Instrucao> instrucoes;

	public void executar(String biblioteca) throws InstrucaoException {
		Biblioteca biblio = cacheBiblioteca.get(biblioteca);
		Metodo metodo = biblio.getMetodo("principal");
		while (metodo != null) {
			Instrucao instrucao = metodo.get();
			instrucao.executar(pilhaMetodo, pilhaOperando, cacheBiblioteca);
			metodo = pilhaMetodo.get();
		}
	}

	static {
		instrucoes = new HashMap<>();
	}
}