package br.com.persist.plugins.consulta;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class ConsultaCor {
	private static final Logger LOG = Logger.getGlobal();
	private final MutableAttributeSet attDestaque;
	private final MutableAttributeSet attNormal;
	private Analise analise = new Analise();
	private final List<String> lista;

	public ConsultaCor() {
		attDestaque = new SimpleAttributeSet();
		attNormal = new SimpleAttributeSet();
		StyleConstants.setForeground(attDestaque, Color.BLUE);
		StyleConstants.setForeground(attNormal, Color.BLACK);
		StyleConstants.setBold(attDestaque, true);
		StyleConstants.setBold(attNormal, false);
		lista = new ArrayList<>();
		abrir();
	}

	public void processar(StyledDocument doc) {
		try {
			analise.set(doc.getText(0, doc.getLength()));
			Token token = analise.get();
			while (token != null) {
				if (token.checar) {
					token.att = lista.contains(token.str) ? attDestaque : attNormal;
				} else {
					token.att = attNormal;
				}
				doc.setCharacterAttributes(token.pos, token.len, token.att, true);
				token = analise.get();
			}
		} catch (BadLocationException e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	private void abrir() {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("consulta")))) {
			String string = br.readLine();
			while (!Util.estaVazio(string)) {
				lista.add(string);
				string = br.readLine();
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}
}

class Analise {
	StringBuilder sb = new StringBuilder();
	Token token = new Token();
	String str;
	int i;

	void set(String s) {
		str = s;
		i = 0;
	}

	Token get() {
		sb.delete(0, sb.length());
		int pos = i;
		int len = 0;
		while (i < str.length()) {
			char c = str.charAt(i);
			if (ok(c)) {
				sb.append(c);
				len++;
				i++;
			} else {
				break;
			}
		}
		if (sb.length() > 0) {
			token.str = sb.toString();
			token.checar = true;
			token.pos = pos;
			token.len = len;
			return token;
		} else {
			while (i < str.length()) {
				char c = str.charAt(i);
				if (!ok(c)) {
					sb.append(c);
					len++;
					i++;
				} else {
					break;
				}
			}
			if (sb.length() > 0) {
				token.checar = false;
				token.str = null;
				token.pos = pos;
				token.len = len;
				return token;
			}
		}
		return null;
	}

	boolean ok(char c) {
		return c >= 'A' && c <= 'Z';
	}
}

class Token {
	MutableAttributeSet att;
	boolean checar;
	String str;
	int pos;
	int len;
}