package br.com.persist.plugins.instrucao;

import br.com.persist.plugins.instrucao.inst.Return;

public class Processador {
	private final CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final PilhaOperando pilhaOperando = new PilhaOperando();
	private final PilhaMetodo pilhaMetodo = new PilhaMetodo();

	public void executar(String biblioteca) throws InstrucaoException {
		Biblioteca biblio = cacheBiblioteca.get(biblioteca);
		Metodo metodo = biblio.getMetodo("principal");
		if (metodo == null) {
			throw new InstrucaoException("erro.biblio_sem_metodo_principal", biblioteca);
		}
		pilhaMetodo.add(metodo);
		while (metodo != null) {
			Instrucao instrucao = metodo.get();
			instrucao.executar(pilhaMetodo, pilhaOperando, cacheBiblioteca);
			metodo = pilhaMetodo.ref();
		}
	}

	static {
		Instrucoes.add(new Return(null));
	}
}