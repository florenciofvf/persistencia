package br.com.persist.plugins.check;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.check.proc.Procedimentos;

public class Sentenca {
	private final List<Procedimento> procedimentos;
	private Procedimento selecionado;
	private String string;

	public Sentenca() {
		procedimentos = new ArrayList<>();
	}

	public PilhaResultParam check(Map<String, Object> map) {
		PilhaResultParam pilha = new PilhaResultParam();
		int i = procedimentos.size() - 1;
		while (i >= 0) {
			Procedimento p = procedimentos.get(i).clonar();
			p.processar(map, pilha);
			i--;
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