package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public abstract class ListaMapaContexto extends Container {
	private static final String ILIST_HEAD = "ilist.head";
	private static final String ILIST_TAIL = "ilist.tail";
	private static final String IMAP_GET = "imap.get";

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
	public void mapa(Compilador compilador, Token token) throws InstrucaoException {
		String string = token.getString();
		int pos = string.indexOf('.');
		String head = string.substring(1, pos);
		String tail = string.substring(pos + 1, string.length() - 1);
		ParametroContexto param = getParametroContexto(head);
		if (param != null) {
			if (param.isTail(head)) {
				compilador.invalidar(token);
			} else if (param.isHead(head)) {
				adicionarListMap(compilador, token, param, tail);
			} else {
				adicionarMap(compilador, token, param, tail);
			}
		} else {
			adicionarConst(compilador, token, head, tail);
		}
	}

	private void adicionarListMap(Compilador compilador, Token token, ParametroContexto param, String chave)
			throws InstrucaoException {
		ArgumentoContexto listHead = criarInvocacao(ILIST_HEAD);
		listHead.adicionar(criarIdentity(param));
		ArgumentoContexto mapGet = criarInvocacao(IMAP_GET);
		mapGet.adicionar(listHead);
		mapGet.adicionar(new StringContexto(tokenString(chave)));
		adicionarImpl(compilador, token, mapGet);
	}

	private void adicionarMap(Compilador compilador, Token token, ParametroContexto param, String chave)
			throws InstrucaoException {
		ArgumentoContexto mapGet = criarInvocacao(IMAP_GET);
		mapGet.adicionar(criarIdentity(param));
		mapGet.adicionar(new StringContexto(tokenString(chave)));
		adicionarImpl(compilador, token, mapGet);
	}

	private void adicionarConst(Compilador compilador, Token token, String head, String chave)
			throws InstrucaoException {
		ArgumentoContexto mapGet = criarInvocacao(IMAP_GET);
		mapGet.adicionar(criarIdentity(tokenString(head)));
		mapGet.adicionar(new StringContexto(tokenString(chave)));
		adicionarImpl(compilador, token, mapGet);
	}

	private Token tokenString(String string) {
		return new Token(string, Tipo.STRING, -1);
	}

	private IdentityContexto criarIdentity(ParametroContexto param) {
		return new IdentityContexto(param.getToken());
	}

	private IdentityContexto criarIdentity(Token token) {
		return new IdentityContexto(token);
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
			ArgumentoContexto invocar = criarInvocacao(ILIST_HEAD);
			invocar.adicionar(id);
			adicionarImpl(compilador, token, invocar);
		} else if (param.isTail(token.getString())) {
			ArgumentoContexto invocar = criarInvocacao(ILIST_TAIL);
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