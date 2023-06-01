package br.com.persist.plugins.instrucao.cmpl;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class InstrucaoAtom {
	private final String string;
	private int indice;

	public InstrucaoAtom(String string) {
		this.string = string.trim();
		indice = 0;
	}

	private Atom throwInstrucaoException() throws InstrucaoException {
		throw new InstrucaoException(string.substring(0, indice), false);
	}

	private boolean indiceAtualEh(char c) {
		if (indice >= string.length()) {
			return false;
		}
		return string.charAt(indice) == c;
	}

	private void pularDescartaveis() {
		while (indice < string.length()) {
			if (string.charAt(indice) <= ' ') {
				indice++;
			} else {
				break;
			}
		}
	}

	private Atom proximoAtom() throws InstrucaoException {
		if (indice >= string.length()) {
			return null;
		}
		pularDescartaveis();
		if (indice >= string.length()) {
			return null;
		}
		return proximoAtomImpl();
	}

	private Atom proximoAtomImpl() throws InstrucaoException {
		char c = string.charAt(indice);
		switch (c) {
		case '\'':
			indice++;
			return atomString();
		case '$':
			indice++;
			return atomVariavel(c);
		case '(':
			indice++;
			return new Atom(c, Atom.PARENTESE_INI);
		case ')':
			indice++;
			return new Atom(c, Atom.PARENTESE_FIM);
		case '{':
			indice++;
			return new Atom(c, Atom.CHAVE_INI);
		case '}':
			indice++;
			return new Atom(c, Atom.CHAVE_FIM);
		case ',':
			indice++;
			return new Atom(c, Atom.VIRGULA);
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '^':
			indice++;
			return new Atom(c, Atom.FUNCAO_INFIXA);
		case '=':
			indice++;
			if (indiceAtualEh('=')) {
				indice++;
				return new Atom("==", Atom.FUNCAO_INFIXA);
			}
			return throwInstrucaoException();
		case '!':
			indice++;
			if (indiceAtualEh('=')) {
				indice++;
				return new Atom("!=", Atom.FUNCAO_INFIXA);
			}
			return new Atom(c, Atom.AUTO);
		case '<':
			indice++;
			if (indiceAtualEh('=')) {
				indice++;
				return new Atom("<=", Atom.FUNCAO_INFIXA);
			}
			return new Atom(c, Atom.FUNCAO_INFIXA);
		case '>':
			indice++;
			if (indiceAtualEh('=')) {
				indice++;
				return new Atom(">=", Atom.FUNCAO_INFIXA);
			}
			return new Atom(c, Atom.FUNCAO_INFIXA);
		case '&':
			indice++;
			if (indiceAtualEh('&')) {
				indice++;
				return new Atom("&&", Atom.FUNCAO_INFIXA);
			}
			return throwInstrucaoException();
		case '|':
			indice++;
			if (indiceAtualEh('|')) {
				indice++;
				return new Atom("||", Atom.FUNCAO_INFIXA);
			}
			return throwInstrucaoException();
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			indice++;
			return atomNumero(c);
		default:
			return criarStringAtom(c);
		}
	}

	private Atom atomString() throws InstrucaoException {
		StringBuilder sb = new StringBuilder();
		boolean escapeAtivado = false;
		boolean encerrado = false;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\'') {
				if (escapeAtivado) {
					sb.append(c);
					escapeAtivado = false;
				} else {
					encerrado = true;
					break;
				}
			} else if (c == '\\') {
				if (escapeAtivado) {
					sb.append(c);
					escapeAtivado = false;
				} else {
					escapeAtivado = true;
				}
			} else {
				checarEscapeAtivado(escapeAtivado);
				sb.append(c);
			}
			indice++;
		}
		if (!encerrado) {
			throwInstrucaoException();
		}
		indice++;
		return new Atom(sb.toString(), Atom.STRING);
	}

	private void checarEscapeAtivado(boolean escapeAtivado) throws InstrucaoException {
		if (escapeAtivado) {
			throwInstrucaoException();
		}
	}

	private Atom atomVariavel(char d) {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (validoChar2(c)) {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		String sequencia = sb.toString();
		return new Atom(sequencia, Atom.VARIAVEL);
	}

	private boolean validoChar2(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '$';
	}

	private Atom atomNumero(char d) throws InstrucaoException {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if ((c >= '0' && c <= '9') || c == '.') {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		String sequencia = sb.toString();
		if (sequencia.indexOf('.') != -1) {
			checarBigDecimal(sequencia);
			return new Atom(sequencia, Atom.BIG_DECIMAL);
		} else {
			return new Atom(sequencia, Atom.BIG_INTEGER);
		}
	}

	private void checarBigDecimal(String sequencia) throws InstrucaoException {
		int total = 0;
		for (char c : sequencia.toCharArray()) {
			if (c == '.') {
				total++;
			}
		}
		if (total != 1 || sequencia.endsWith(".")) {
			throwInstrucaoException();
		}
	}

	private Atom criarStringAtom(char c) throws InstrucaoException {
		if (c >= 'a' && c <= 'z') {
			indice++;
			return stringAtom(c);
		}
		return throwInstrucaoException();
	}

	private Atom stringAtom(char d) throws InstrucaoException {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (validoChar(c)) {
				sb.append(c);
			} else {
				break;
			}
			indice++;
		}
		String sequencia = sb.toString();
		checarStringAtom(sequencia);
		return new Atom(sequencia, Atom.STRING_ATOM);
	}

	private boolean validoChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '.';
	}

	private void checarStringAtom(String sequencia) throws InstrucaoException {
		int total = 0;
		for (char c : sequencia.toCharArray()) {
			if (c == '.') {
				total++;
			}
		}
		if (total > 1 || sequencia.endsWith(".")) {
			throwInstrucaoException();
		}
	}

	public List<Atom> getListaAtom() throws InstrucaoException {
		List<Atom> lista = new ArrayList<>();
		Atom atom = proximoAtom();
		while (atom != null) {
			lista.add(atom);
			atom = proximoAtom();
		}
		normalizar(lista, "-", true);
		normalizar(lista, "+", false);
		return lista;
	}

	class AtomIndice {
		final Atom atom;
		final int indice;

		public AtomIndice(Atom atom, int indice) {
			this.indice = indice;
			this.atom = atom;
		}
	}

	private void normalizar(final List<Atom> lista, final String valor, boolean negar) throws InstrucaoException {
		AtomIndice atomIndice = getAtomIndiceParaValor(lista, valor);
		while (atomIndice != null) {
			processar(lista, atomIndice, negar);
			atomIndice = getAtomIndiceParaValor(lista, valor);
		}
	}

	private AtomIndice getAtomIndiceParaValor(final List<Atom> lista, Object valor) {
		for (int i = 0; i < lista.size(); i++) {
			Atom atom = lista.get(i);
			if (!atom.isProcessado() && valor.equals(atom.getValor())) {
				return new AtomIndice(atom, i);
			}
		}
		return null;
	}

	private void processar(final List<Atom> lista, AtomIndice atomIndice, boolean negar) throws InstrucaoException {
		int indiceAtomAntes = atomIndice.indice - 1;
		int indiceAtomDepois = atomIndice.indice + 1;
		if (!ehOperandoCalculavel(indiceAtomAntes, lista) && isAtomNumero(indiceAtomDepois, lista)) {
			if (negar) {
				negarAtom(indiceAtomDepois, atomIndice, lista);
			} else {
				lista.remove(atomIndice.indice);
			}
		} else if (!ehOperandoCalculavel(indiceAtomAntes, lista) && iniExpressao(indiceAtomDepois, lista)) {
			if (negar) {
				lista.get(indiceAtomDepois).setNegarExpressao(true);
			}
			lista.remove(atomIndice.indice);
		} else {
			atomIndice.atom.setProcessado(true);
		}
	}

	private boolean isAtomNumero(int i, final List<Atom> lista) {
		if (i >= 0 && i < lista.size()) {
			Atom atom = lista.get(i);
			return atom.isBigInteger() || atom.isBigDecimal();
		}
		return false;
	}

	private boolean iniExpressao(int i, final List<Atom> lista) {
		if (i >= 0 && i < lista.size()) {
			Atom atom = lista.get(i);
			return atom.isParenteseIni();
		}
		return false;
	}

	private boolean ehOperandoCalculavel(int i, List<Atom> lista) {
		if (i >= 0 && i < lista.size()) {
			Atom atom = lista.get(i);
			return atom.isParenteseFim() || atom.isVariavel() || atom.isBigInteger() || atom.isBigDecimal();
		}
		return false;
	}

	private void negarAtom(int i, AtomIndice atomIndice, final List<Atom> lista) throws InstrucaoException {
		Atom atom = lista.get(i);
		Atom novo = inverter(atom, i);
		lista.set(i, novo);
		lista.remove(atomIndice.indice);
	}

	private Atom inverter(Atom atom, int indice) throws InstrucaoException {
		if (atom.isBigInteger()) {
			return new Atom("-" + atom.getValor(), Atom.BIG_INTEGER);
		} else if (atom.isBigDecimal()) {
			return new Atom("-" + atom.getValor(), Atom.BIG_DECIMAL);
		}
		return throwInstrucaoException();
	}
}