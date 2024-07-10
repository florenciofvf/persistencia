package br.com.persist.plugins.instrucao.compilador.biblio;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class BibliotecaContexto extends Container {
	private final BibliotecaCorpoContexto corpo;

	public BibliotecaContexto() {
		corpo = new BibliotecaCorpoContexto();
		adicionar(corpo);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if(finalizado) {
			compilador.invalidar(token);
		}
		if ("{".equals(token.getString())) {
			compilador.setContexto(corpo);
		} else {
			compilador.invalidar(token);
		}
	}
}