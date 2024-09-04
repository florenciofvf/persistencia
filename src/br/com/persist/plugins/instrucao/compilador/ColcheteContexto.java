package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public class ColcheteContexto extends Container {
	private final boolean exp;

	public ColcheteContexto(boolean exp) {
		this.exp = exp;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!"]".equals(token.getString())) {
			compilador.invalidar(token);
		}
		compilador.setContexto(getPai());
		normalizar(compilador);
	}

	private void normalizar(Compilador compilador) throws InstrucaoException {
		Container paiBkp = getPai();
		getPai().excluir(this);
		if (isEmpty()) {
			Token token = new Token("ilist.create", Tipo.IDENTITY, -1);
			if (exp) {
				IdentityContexto identity = new IdentityContexto(token);
				paiBkp.adicionar(new ArgumentoContexto(identity));
				return;
			} else {
				compilador.invalidar(token);
			}
		}
		IdentityContexto identity = (IdentityContexto) excluir(0);
		if (exp) {
			ArgumentoContexto argumento = new ArgumentoContexto(identity);
			paiBkp.adicionar(argumento);
			adicionarEm(argumento);
		} else {
			InvocacaoContexto invocacao = new InvocacaoContexto(identity.token);
			paiBkp.adicionar(invocacao);
			adicionarEm(invocacao.getArgumento());
		}
	}

	private void adicionarEm(Container c) {
		Container container = excluir(0);
		while (container != null) {
			c.adicionar(container);
			container = excluir(0);
		}
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		checar(compilador, token);
		adicionar(new StringContexto(token));
	}

	@Override
	public void numero(Compilador compilador, Token token) throws InstrucaoException {
		checar(compilador, token);
		adicionar(new NumeroContexto(token));
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		adicionar(new IdentityContexto(token));
	}

	private void checar(Compilador compilador, Token token) throws InstrucaoException {
		if (isEmpty()) {
			compilador.invalidar(token);
		}
	}
}