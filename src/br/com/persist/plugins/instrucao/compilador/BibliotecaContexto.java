package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class BibliotecaContexto extends Container {
	private final String nome;

	public BibliotecaContexto(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if ("function".equals(token.getString())) {
			compilador.setContexto(new FuncaoContexto());
			adicionar((Container) compilador.getContexto());
		} else if ("function_native".equals(token.getString())) {
			compilador.setContexto(new FuncaoNativaContexto());
			adicionar((Container) compilador.getContexto());
		} else if ("const".equals(token.getString())) {
			compilador.setContexto(new ConstanteContexto());
			adicionar((Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public String toString() {
		return nome;
	}
}