package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ConstanteContexto extends Container {
	public static final String LOAD_CONST = "load_const";
	private final ConstanteIdentityContexto identity;
	private boolean faseIdentity;

	public ConstanteContexto() {
		ExpressaoContexto expressao = new ExpressaoContexto();
		expressao.adicionar(new ExpressaoContexto());
		identity = new ConstanteIdentityContexto();
		contexto = Contextos.ABRE_PARENTESES;
		adicionar(expressao);
		faseIdentity = true;
	}

	public ExpressaoContexto getExpressao() {
		return (ExpressaoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseIdentity) {
			contexto = identity;
		} else {
			compilador.setContexto(getExpressao().getUltimo());
			contexto = Contextos.PONTO_VIRGULA;
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		Container valor = getExpressao().getUltimo();
		if (valor.isEmpty()) {
			throw new InstrucaoException("Valor indefinido para: " + identity.token.string, false);
		}
		compilador.setContexto(getPai());
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.separador(compilador, token);
		contexto = Contextos.ABRE_PARENTESES;
		faseIdentity = false;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		contexto = Contextos.VIRGULA;
	}

	public void indexar() {
		// TODO
	}

	@Override
	public String toString() {
		return identity.toString();
	}
}

class ConstanteIdentityContexto extends AbstratoContexto {
	Token token;

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}

	@Override
	public String toString() {
		return token.getString();
	}
}