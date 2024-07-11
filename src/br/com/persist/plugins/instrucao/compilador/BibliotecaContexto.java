package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.funcao.FuncaoContexto;

public class BibliotecaContexto extends Container {
	private final BibliotecaCorpoContexto corpo;
	private Contexto contexto;

	public BibliotecaContexto() {
		corpo = new BibliotecaCorpoContexto();
		contexto = Contextos.ABRE_CHAVES;
		adicionar(corpo);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(corpo);
		contexto = Contextos.INVALIDO;
	}
}

class BibliotecaCorpoContexto extends Container {
	private Contexto contexto;

	BibliotecaCorpoContexto() {
		contexto = Contextos.FECHA_CHAVES;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if ("function".equals(token.getString())) {
			compilador.setContexto(new FuncaoContexto());
			adicionar((Container) compilador.getContexto());
		} else if ("const".equals(token.getString())) {
			compilador.setContexto(new ConstanteContexto());
			adicionar((Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}
}