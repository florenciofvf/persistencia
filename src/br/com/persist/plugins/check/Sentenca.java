package br.com.persist.plugins.check;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import br.com.persist.assistencia.Util;

public class Sentenca {
	private final List<Procedimento> procedimentos;
	private Procedimento selecionado;
	private String string;

	public Sentenca() {
		procedimentos = new ArrayList<>();
	}

	public PilhaResultParam check(Map<String, Object> map) {
		PilhaResultParam pilha = new PilhaResultParam();
		ListIterator<Procedimento> it = procedimentos.listIterator();
		while (it.hasPrevious()) {
			Procedimento p = it.previous().clonar();
			p.processar(map, pilha);
		}
		return pilha;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

	public void inicializar() {
		if (string == null || string.trim().length() == 0) {
			throw new IllegalStateException("Sentenca vazia.");
		}
		List<Token> tokens = Token.criarTokens(string);
		Iterator<Token> it = tokens.iterator();
		while (it.hasNext()) {
			Token token = it.next();
			if (token instanceof TokenMetodoIni) {
				novoMetodo((TokenMetodoIni) token);
			} else if (token instanceof TokenMetodoFim) {
				finalMetodo((TokenMetodoFim) token);
			} else if (token instanceof TokenParam) {
				parametro((TokenParam) token);
			}
		}
	}

	private void novoMetodo(TokenMetodoIni token) {
		Procedimento p = Procedimentos.get(token.getString());
		procedimentos.add(p);
		p.pai = selecionado;
		selecionado = p;
	}

	private void finalMetodo(TokenMetodoFim token) {
		String param = token.getString();
		if (!Util.estaVazio(param)) {
			selecionado.addParam(param);
		}
		selecionado = selecionado.pai;
	}

	private void parametro(TokenParam token) {
		String param = token.getString();
		if (!Util.estaVazio(param)) {
			selecionado.addParam(param);
		}
	}
}