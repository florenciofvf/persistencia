package br.com.persist.plugins.instrucao;

public class Processador {
	private final CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final PilhaOperando pilhaOperando = new PilhaOperando();
	private final PilhaMetodo pilhaMetodo = new PilhaMetodo();

	public void executar(String biblioteca) {
		Biblioteca biblio = cacheBiblioteca.get(biblioteca);
		Metodo metodo = biblio.getMetodo("principal");
		while (metodo != null) {
			Instrucao instrucao = metodo.get();
			instrucao.executar(pilhaMetodo, pilhaOperando, cacheBiblioteca);
			metodo = pilhaMetodo.get();
		}
	}
}