package br.com.persist.fmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import br.com.persist.util.Util;

public class Parser {
	private Tipo selecionado;
	private Atom ultimoAtom;
	private String string;
	private int indice;
	private Atom atom;

	public Tipo parse(File file) throws IOException {
		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String linha = br.readLine();

			while (linha != null) {
				sb.append(linha.trim());
				linha = br.readLine();
			}
		}

		return parse(sb.toString());
	}

	private void processarDadosBasicos() {
		if (selecionado instanceof Array) {
			((Array) selecionado).adicionar(atom.valor);
			ultimoAtom = null;
		} else {
			ultimoAtom = atom;
		}
	}

	private void processarDoisPontos() {
		int bkp = indice;
		gerarAtom();
		checarAtom();

		if (dadosBasicos(atom)) {
			((Objeto) selecionado).atributo(ultimoAtom.valor.toString(), atom.valor);
			ultimoAtom = null;

		} else {
			indice = bkp;
		}
	}

	public Tipo parse(String string) {
		Tipo raiz = null;
		indice = 0;

		if (Util.estaVazio(string)) {
			return null;
		}

		this.string = string.trim();

		gerarAtom();

		if (atom == null) {
			return null;
		}

		checarAtom();

		if (atom.tipo == Atom.CHAVE_INI) {
			raiz = new Objeto();
		} else if (atom.tipo == Atom.COLCH_INI) {
			raiz = new Array();
		} else {
			throw new IllegalStateException();
		}

		selecionado = raiz;

		gerarAtom();

		while (atom != null) {
			checarAtom();

			if (dadosBasicos(atom)) {
				processarDadosBasicos();
			} else if (fimObjeto(atom)) {
				selecionado = selecionado.pai;
			} else if (atom.tipo == Atom.CHAVE_INI) {
				adicionar(new Objeto());
			} else if (atom.tipo == Atom.COLCH_INI) {
				adicionar(new Array());
			} else if (atom.tipo == Atom.DOIS_PONT) {
				processarDoisPontos();
			}

			gerarAtom();
		}

		return raiz;
	}

	private void adicionar(Tipo tipo) {
		if (selecionado instanceof Objeto) {
			((Objeto) selecionado).atributo(ultimoAtom.valor.toString(), tipo);
			selecionado = tipo;
			ultimoAtom = null;

		} else if (selecionado instanceof Array) {
			((Array) selecionado).adicionar(tipo);
			selecionado = tipo;
			ultimoAtom = null;

		} else {
			throw new IllegalStateException();
		}
	}

	private boolean dadosBasicos(Atom atom) {
		return atom.tipo == Atom.LOGICO || atom.tipo == Atom.NULL || atom.tipo == Atom.NUMERO
				|| atom.tipo == Atom.TEXTO;
	}

	private boolean fimObjeto(Atom atom) {
		return atom.tipo == Atom.CHAVE_FIM || atom.tipo == Atom.COLCH_FIM;
	}

	private boolean atom1(char c) {
		return c == '[' || c == ']' || c == '{' || c == '}' || c == ':' || c == ',';
	}

	private boolean numero(char c) {
		return (c >= '0' && c <= '9') || c == '+' || c == '-';
	}

	private boolean literal(char c) {
		return c == 't' || c == 'f' || c == 'n';
	}

	private void gerarAtom() {
		int delta = 1;
		atom = null;
		avancar();

		while (indice < string.length()) {
			char c = string.charAt(indice);

			if (atom1(c)) {
				criarAtom1(c);

			} else if (c == '\"') {
				indice++;
				atom = new Atom(Atom.TEXTO, criarAtomString());
				indice--;

			} else if (literal(c)) {
				String s = string.substring(indice);

				if (s.startsWith("true")) {
					atom = new Atom(Atom.LOGICO, Boolean.TRUE);
					delta = 4;

				} else if (s.startsWith("false")) {
					atom = new Atom(Atom.LOGICO, Boolean.FALSE);
					delta = 5;

				} else if (s.startsWith(Null.CONTEUDO)) {
					atom = new Atom(Atom.NULL, null);
					delta = 4;

				} else {
					throw new IllegalStateException();
				}

			} else if (numero(c)) {
				atom = new Atom(Atom.NUMERO, criarAtomNumero());
				indice--;

			} else {
				atom = new Atom(Atom.INVALIDO);
			}

			indice += delta;

			if (delta > 0) {
				break;
			}
		}
	}

	private String criarAtomString() {
		StringBuilder sb = new StringBuilder();

		while (indice < string.length()) {
			char c = string.charAt(indice);

			if (c == '\"' && (sb.length() == 0 || sb.charAt(sb.length() - 1) != '\\')) {
				indice++;
				break;
			}

			sb.append(c);
			indice++;
		}

		return sb.toString();
	}

	private Number criarAtomNumero() {
		StringBuilder sb = new StringBuilder();

		while (indice < string.length()) {
			char c = string.charAt(indice);

			if (c == '.' || numero(c)) {
				sb.append(c);
				indice++;
			} else {
				break;
			}
		}

		String s = sb.toString();

		return s.indexOf('.') >= 0 ? (Number) Double.valueOf(s) : (Number) Long.valueOf(s);
	}

	private void criarAtom1(char c) {
		if (c == '[') {
			atom = new Atom(Atom.COLCH_INI);
		} else if (c == ']') {
			atom = new Atom(Atom.COLCH_FIM);
		} else if (c == '{') {
			atom = new Atom(Atom.CHAVE_INI);
		} else if (c == '}') {
			atom = new Atom(Atom.CHAVE_FIM);
		} else if (c == ':') {
			atom = new Atom(Atom.DOIS_PONT);
		} else if (c == ',') {
			atom = new Atom(Atom.VIRGULA);
		}
	}

	private void checarAtom() {
		if (atom.tipo == Atom.INVALIDO) {
			throw new IllegalStateException();
		}
	}

	private void avancar() {
		while (indice < string.length()) {
			char c = string.charAt(indice);

			if (c > ' ') {
				break;
			}

			indice++;
		}
	}
}