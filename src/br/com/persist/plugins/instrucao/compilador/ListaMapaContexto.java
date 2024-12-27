package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public abstract class ListaMapaContexto extends Container {
	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new StringContexto(token));
	}

	@Override
	public void numero(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new NumeroContexto(token));
	}

	@Override
	public void lista(Compilador compilador, Token token) throws InstrucaoException {
		ParametroContexto param = getParametroContexto(token.getString());
		if (param != null || ListaContexto.ehListaVazia(token.getString())) {
			adicionarImpl(compilador, token, new ListaContexto(token));
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		ParametroContexto param = getParametroContexto(token.getString());
		if (param == null) {
			adicionarImpl(compilador, token, new IdentityContexto(token));
		} else {
			processar(compilador, param, token);
		}
	}

	private void processar(Compilador compilador, ParametroContexto param, Token token) throws InstrucaoException {
		IdentityContexto id = new IdentityContexto(param.getToken());
		if (param.isHead(token.getString())) {
			ArgumentoContexto invocar = criarInvocacao("ilist.head");
			invocar.adicionar(id);
			adicionarImpl(compilador, token, invocar);
		} else if (param.isTail(token.getString())) {
			ArgumentoContexto invocar = criarInvocacao("ilist.tail");
			invocar.adicionar(id);
			adicionarImpl(compilador, token, invocar);
		} else {
			adicionarImpl(compilador, token, id);
		}
	}

	private ArgumentoContexto criarInvocacao(String string) {
		Token token = new Token(string, Tipo.IDENTITY, -1);
		IdentityContexto identity = new IdentityContexto(token);
		return new ArgumentoContexto(identity);
	}

	protected abstract void adicionarImpl(Compilador compilador, Token token, Container c) throws InstrucaoException;
}