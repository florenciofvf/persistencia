package br.com.persist.plugins.check;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sentenca {
	private static final Logger LOG = Logger.getGlobal();
	private final List<Procedimento> procedimentos;
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
		List<Token> tokens = Token.criarTokens(string);
		Iterator<Token> it = tokens.iterator();
		while (it.hasNext()) {
			processar(it.next());
		}
	}

	private void processar(TokenMetodoIni token) {
	}

	private void processar(TokenMetodoFim token) {

	}

	private void processar(TokenParam token) {
	}

	private void processar(Token next) {
		LOG.log(Level.FINEST, "processar(Token)");
	}
}