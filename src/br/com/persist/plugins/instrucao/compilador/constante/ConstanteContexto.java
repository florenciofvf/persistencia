package br.com.persist.plugins.instrucao.compilador.constante;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.AbstratoContexto;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Contexto;
import br.com.persist.plugins.instrucao.compilador.Contextos;
import br.com.persist.plugins.instrucao.compilador.Token;
import br.com.persist.plugins.instrucao.compilador.expressao.ExpressaoContexto;

public class ConstanteContexto extends Container {
	private final Identity identity = new Identity();
	private Contexto contexto;
	private byte fase;

	public ConstanteContexto() {
		contexto = Contextos.ABRE_PARENTESES;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if (fase == 0) {
			contexto.inicializador(compilador, token);
			contexto = identity;
		} else {
			contexto.inicializador(compilador, token);
			compilador.setContexto(new ExpressaoContexto());
			adicionar((Container) compilador.getContexto());
			contexto = Contextos.PONTO_VIRGULA;
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.separador(compilador, token);
		contexto = Contextos.ABRE_PARENTESES;
		fase++;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		contexto = Contextos.SEPARADOR;
	}
}

class Identity extends AbstratoContexto {
	Token token;

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}
}