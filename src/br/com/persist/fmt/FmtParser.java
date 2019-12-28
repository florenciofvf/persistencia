package br.com.persist.fmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FmtParser {
	private Atomico ultimoAtomico;
	private Tipo selecionado;
	private Atomico atomico;
	private String string;
	private int indice;

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

	public Tipo parse(String string) {
		Tipo raiz = null;
		indice = 0;

		if (string != null) {
			this.string = string.trim();
		} else {
			return null;
		}

		gerarAtomico();
		if (atomico == null) {
			return null;
		}
		checarAtomico();

		if (atomico.tipo == Atomico.CHAVE_INI) {
			raiz = new Objeto();
		} else if (atomico.tipo == Atomico.COLCH_INI) {
			raiz = new Array();
		} else {
			throw new IllegalStateException();
		}

		selecionado = raiz;
		gerarAtomico();

		while (atomico != null) {
			checarAtomico();

			if (dadosBasicos(atomico)) {
				if (selecionado instanceof Array) {
					((Array) selecionado).adicionar(atomico.valor);
					ultimoAtomico = null;
				} else {
					ultimoAtomico = atomico;
				}

			} else if (fimObjeto(atomico)) {
				selecionado = selecionado.pai;

			} else if (atomico.tipo == Atomico.CHAVE_INI) {
				adicionar(new Objeto());

			} else if (atomico.tipo == Atomico.COLCH_INI) {
				adicionar(new Array());

			} else if (atomico.tipo == Atomico.DOIS_PONT) {
				int bkp = indice;
				gerarAtomico();
				checarAtomico();

				if (dadosBasicos(atomico)) {
					((Objeto) selecionado).atributo(ultimoAtomico.valor.toString(), atomico.valor);
					ultimoAtomico = null;

				} else {
					indice = bkp;
				}

			}

			gerarAtomico();
		}

		return raiz;
	}

	private void adicionar(Tipo tipo) {
		if (selecionado instanceof Objeto) {
			((Objeto) selecionado).atributo(ultimoAtomico.valor.toString(), tipo);
			selecionado = tipo;
			ultimoAtomico = null;

		} else if (selecionado instanceof Array) {
			((Array) selecionado).adicionar(tipo);
			selecionado = tipo;
			ultimoAtomico = null;

		} else {
			throw new IllegalStateException();
		}
	}

	private boolean dadosBasicos(Atomico atomico) {
		return atomico.tipo == Atomico.LOGICO || atomico.tipo == Atomico.NUMERO || atomico.tipo == Atomico.TEXTO;
	}

	private boolean fimObjeto(Atomico atomico) {
		return atomico.tipo == Atomico.CHAVE_FIM || atomico.tipo == Atomico.COLCH_FIM;
	}

	private boolean atomico1(char c) {
		return c == '[' || c == ']' || c == '{' || c == '}' || c == ':' || c == ',';
	}

	private boolean numero(char c) {
		return c >= '0' && c <= '9';
	}

	private void gerarAtomico() {
		atomico = null;
		int delta = 1;
		avancar();

		while (indice < string.length()) {
			char c = string.charAt(indice);

			if (atomico1(c)) {
				criarAtomico1(c);

			} else if (c == '\"') {
				indice++;
				atomico = new Atomico(Atomico.TEXTO, criarAtomicoString());
				indice--;

			} else if (c == 't' || c == 'f') {
				String s = string.substring(indice);

				if (s.startsWith("true")) {
					atomico = new Atomico(Atomico.LOGICO, true);
					delta = 4;

				} else if (s.startsWith("false")) {
					atomico = new Atomico(Atomico.LOGICO, false);
					delta = 5;

				} else {
					throw new IllegalStateException();
				}

			} else if (numero(c)) {
				atomico = new Atomico(Atomico.NUMERO, criarAtomicoNumero());

			} else {
				atomico = new Atomico(Atomico.INVALIDO);
			}

			indice += delta;

			if (delta > 0) {
				break;
			}
		}
	}

	private String criarAtomicoString() {
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

	private String criarAtomicoNumero() {
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

		return sb.toString();
	}

	private void criarAtomico1(char c) {
		if (c == '[') {
			atomico = new Atomico(Atomico.COLCH_INI);
		} else if (c == ']') {
			atomico = new Atomico(Atomico.COLCH_FIM);
		} else if (c == '{') {
			atomico = new Atomico(Atomico.CHAVE_INI);
		} else if (c == '}') {
			atomico = new Atomico(Atomico.CHAVE_FIM);
		} else if (c == ':') {
			atomico = new Atomico(Atomico.DOIS_PONT);
		} else if (c == ',') {
			atomico = new Atomico(Atomico.VIRGULA);
		}
	}

	private void checarAtomico() {
		if (atomico.tipo == Atomico.INVALIDO) {
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