package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ArquivoContexto extends Container {
	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		throwInstrucaoException(token);
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		throwInstrucaoException(token);
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		throwInstrucaoException(token);
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		throwInstrucaoException(token);
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if ("function".equals(token.string)) {
//			FuncaoContexto funcao = new FuncaoContexto();
//			compilador.contexto = funcao;
//			adicionar(funcao);
		} else if ("const".equals(token.string)) {
//			ConstanteContexto constante = new ConstanteContexto();
//			compilador.contexto = constante;
//			adicionar(constante);
		} else {
			throwInstrucaoException(token);
		}
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		throwInstrucaoException(token);
	}

	@Override
	public void numero(Compilador compilador, Token token) throws InstrucaoException {
		throwInstrucaoException(token);
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		throwInstrucaoException(token);
	}
}