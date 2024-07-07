package br.com.persist.plugins.instrucao.compilador.biblio;

import br.com.persist.plugins.instrucao.compilador.Container;

public class BibliotecaContexto extends Container {
	private final BibliotecaCorpoContexto corpo;

	public BibliotecaContexto() {
		corpo = new BibliotecaCorpoContexto();
		adicionar(corpo);
	}

	public BibliotecaCorpoContexto getCorpo() {
		return corpo;
	}
}