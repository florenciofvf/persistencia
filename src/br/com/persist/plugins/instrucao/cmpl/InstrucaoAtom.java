package br.com.persist.plugins.instrucao.cmpl;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class InstrucaoAtom {
	private final List<Atom> comentarios;
	private final String string;
	private int indice;

	public InstrucaoAtom(String string) {
		comentarios = new ArrayList<>();
		this.string = string;
		indice = 0;
	}

	public List<Atom> getComentarios() {
		return comentarios;
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
			return atomString(indice - 1);
		case '$':
			indice++;
			return atomParam(c, indice - 1);
		case '#':
			indice++;
			return atomVariavel(c, indice - 1);
		case '(':
			indice++;
			return new Atom(c, Atom.PARENTESE_INI, indice - 1);
		case ')':
			indice++;
			return new Atom(c, Atom.PARENTESE_FIM, indice - 1);
		case '{':
			indice++;
			return new Atom(c, Atom.CHAVE_INI, indice - 1);
		case '}':
			indice++;
			return new Atom(c, Atom.CHAVE_FIM, indice - 1);
		case ',':
			indice++;
			return new Atom(c, Atom.VIRGULA, indice - 1);
		case '+':
		case '-':
		case '%':
		case '^':
		case ':':
			indice++;
			return new Atom(c, Atom.FUNCAO_INFIXA, indice - 1);
		case '*':
			indice++;
			return infixaMulOrPow(c, indice - 1);
		case '/':
			indice++;
			return infixaDivOrComentario(c, indice - 1);
		case '=':
			indice++;
			return infixaIgual(indice - 1);
		case '!':
			indice++;
			return infixaDiferente(indice - 1);
		case '<':
			indice++;
			return infixaMenor(c, indice - 1);
		case '>':
			indice++;
			return infixaMaior(c, indice - 1);
		case '&':
			indice++;
			return infixaAnd(indice - 1);
		case '|':
			indice++;
			return infixaOr(indice - 1);
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
			return atomNumero(c, indice - 1);
		default:
			return criarStringAtom(c, indice);
		}
	}

	private Atom atomString(int index) throws InstrucaoException {
		StringBuilder sb = new StringBuilder();
		boolean escapeAtivado = false;
		boolean encerrado = false;
		int lengthOffset = 0;
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (c == '\'') {
				if (escapeAtivado) {
					appendC(sb, c);
					escapeAtivado = false;
				} else {
					encerrado = true;
					break;
				}
			} else if (c == '\\') {
				if (escapeAtivado) {
					appendC(sb, c);
					escapeAtivado = false;
				} else {
					escapeAtivado = true;
					lengthOffset++;
				}
			} else {
				checarEscapeAtivado(escapeAtivado);
				appendC(sb, c);
			}
			indice++;
		}
		if (!encerrado) {
			throwInstrucaoException();
		}
		indice++;
		Atom resp = new Atom(sb.toString(), Atom.STRING, index);
		resp.setLengthOffset(lengthOffset + 2);
		return resp;
	}

	private void appendC(StringBuilder sb, char c) {
		if (c == '\r') {
			sb.append(InstrucaoConstantes.CR);
		} else if (c == '\n') {
			sb.append(InstrucaoConstantes.LF);
		} else {
			sb.append(c);
		}
	}

	private void checarEscapeAtivado(boolean escapeAtivado) throws InstrucaoException {
		if (escapeAtivado) {
			throwInstrucaoException();
		}
	}

	private Atom atomVariavel(char d, int index) {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (validoChar2(c)) {
				appendC(sb, c);
			} else {
				break;
			}
			indice++;
		}
		String sequencia = sb.toString();
		return new Atom(sequencia, Atom.VARIAVEL, index);
	}

	private boolean validoChar2(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '$'
				|| c == '#';
	}

	private Atom atomParam(char d, int index) {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (validoChar2(c)) {
				appendC(sb, c);
			} else {
				break;
			}
			indice++;
		}
		String sequencia = sb.toString();
		return new Atom(sequencia, Atom.PARAM, index);
	}

	private Atom infixaMulOrPow(char c, int index) {
		if (indiceAtualEh('*')) {
			indice++;
			return new Atom("**", Atom.FUNCAO_INFIXA, index);
		}
		return new Atom(c, Atom.FUNCAO_INFIXA, index);
	}

	private Atom infixaDivOrComentario(char c, int index) throws InstrucaoException {
		if (indiceAtualEh('*')) {
			indice++;
			return atomComentario(index);
		}
		return new Atom(c, Atom.FUNCAO_INFIXA, index);
	}

	private Atom atomComentario(int index) throws InstrucaoException {
		StringBuilder sb = new StringBuilder("/*");
		boolean encerrado = false;
		char anterior = ' ';
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (anterior == '*' && c == '/') {
				encerrado = true;
				sb.append(c);
				break;
			} else {
				anterior = c;
				sb.append(c);
			}
			indice++;
		}
		if (!encerrado) {
			throwInstrucaoException();
		}
		indice++;
		return new Atom(sb.toString(), Atom.COMENTARIO, index);
	}

	private Atom infixaIgual(int index) throws InstrucaoException {
		if (indiceAtualEh('=')) {
			indice++;
			return new Atom("==", Atom.FUNCAO_INFIXA, index);
		}
		return throwInstrucaoException();
	}

	private Atom infixaDiferente(int index) throws InstrucaoException {
		if (indiceAtualEh('=')) {
			indice++;
			return new Atom("!=", Atom.FUNCAO_INFIXA, index);
		}
		return throwInstrucaoException();
	}

	private Atom infixaMenor(char c, int index) {
		if (indiceAtualEh('=')) {
			indice++;
			return new Atom("<=", Atom.FUNCAO_INFIXA, index);
		}
		return new Atom(c, Atom.FUNCAO_INFIXA, index);
	}

	private Atom infixaMaior(char c, int index) {
		if (indiceAtualEh('=')) {
			indice++;
			return new Atom(">=", Atom.FUNCAO_INFIXA, index);
		}
		return new Atom(c, Atom.FUNCAO_INFIXA, index);
	}

	private Atom infixaAnd(int index) throws InstrucaoException {
		if (indiceAtualEh('&')) {
			indice++;
			return new Atom("&&", Atom.FUNCAO_INFIXA, index);
		}
		return throwInstrucaoException();
	}

	private Atom infixaOr(int index) throws InstrucaoException {
		if (indiceAtualEh('|')) {
			indice++;
			return new Atom("||", Atom.FUNCAO_INFIXA, index);
		}
		return throwInstrucaoException();
	}

	private Atom atomNumero(char d, int index) throws InstrucaoException {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if ((c >= '0' && c <= '9') || c == '.') {
				appendC(sb, c);
			} else {
				break;
			}
			indice++;
		}
		boolean bigDecimal = false;
		if (indiceAtualEh('D') || indiceAtualEh('d')) {
			appendC(sb, string.charAt(indice));
			bigDecimal = true;
			indice++;
		}
		String sequencia = sb.toString();
		if (bigDecimal || sequencia.indexOf('.') != -1) {
			checarBigDecimal(sequencia);
			return new Atom(sequencia, Atom.BIG_DECIMAL, index);
		} else {
			return new Atom(sequencia, Atom.BIG_INTEGER, index);
		}
	}

	private void checarBigDecimal(String sequencia) throws InstrucaoException {
		int total = 0;
		for (char c : sequencia.toCharArray()) {
			if (c == '.') {
				total++;
			}
		}
		if (total > 1 || sequencia.endsWith(".") || sequencia.endsWith(".D") || sequencia.endsWith(".d")) {
			throwInstrucaoException();
		}
	}

	private Atom criarStringAtom(char c, int index) throws InstrucaoException {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			indice++;
			return stringAtom(c, index);
		}
		return throwInstrucaoException();
	}

	private Atom stringAtom(char d, int index) throws InstrucaoException {
		StringBuilder sb = new StringBuilder("" + d);
		while (indice < string.length()) {
			char c = string.charAt(indice);
			if (validoChar(c)) {
				appendC(sb, c);
			} else {
				break;
			}
			indice++;
		}
		String sequencia = sb.toString();
		checarStringAtom(sequencia);
		if (sequencia.indexOf(".#") != -1) {
			return new Atom(sequencia, Atom.VARIAVEL, index);
		}
		return new Atom(sequencia, Atom.STRING_ATOM, index);
	}

	private boolean validoChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '.'
				|| c == '#';
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
			if (!atom.isComentario()) {
				lista.add(atom);
			} else {
				comentarios.add(atom);
			}
			atom = proximoAtom();
		}
		normalizar(lista, "-", true);
		normalizar(lista, "+", false);
		return lista;
	}

	private void normalizar(final List<Atom> lista, final String valor, boolean negar) throws InstrucaoException {
		AtomIndice atomIndice = getAtomIndiceParaValor(lista, valor);
		while (atomIndice != null) {
			processar(lista, atomIndice, negar);
			atomIndice = getAtomIndiceParaValor(lista, valor);
		}
	}

	static AtomIndice getAtomIndiceParaValor(final List<Atom> lista, Object valor) {
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
		processar1(lista, atomIndice, negar, indiceAtomAntes, indiceAtomDepois);
	}

	private void processar1(final List<Atom> lista, AtomIndice atomIndice, boolean negar, int indiceAtomAntes,
			int indiceAtomDepois) throws InstrucaoException {
		if (!ehOperandoCalculavel(indiceAtomAntes, lista, negar) && atomNumero(indiceAtomDepois, lista)) {
			mergeAtom(atomIndice.indice, indiceAtomDepois, atomIndice, lista);
		} else if (!ehOperandoCalculavel(indiceAtomAntes, lista, negar) && iniExpressao(indiceAtomDepois, lista)) {
			checkNegarERemover(lista, atomIndice, negar, indiceAtomDepois);
		} else {
			processar2(lista, atomIndice, negar, indiceAtomAntes, indiceAtomDepois);
		}
	}

	private boolean ehOperandoCalculavel(int i, List<Atom> lista, boolean negar) {
		if (i >= 0 && i < lista.size()) {
			Atom atom = lista.get(i);
			if (negar) {
				return atom.isParenteseFim() || atom.isParam() || atom.isVariavel() || atom.isBigInteger()
						|| atom.isBigDecimal();
			} else {
				return atom.isParenteseFim() || atom.isParam() || atom.isVariavel() || atom.isBigInteger()
						|| atom.isBigDecimal() || atom.isString();
			}
		}
		return false;
	}

	private boolean atomNumero(int i, final List<Atom> lista) {
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

	private void checkNegarERemover(final List<Atom> lista, AtomIndice atomIndice, boolean negar,
			int indiceAtomDepois) {
		if (negar) {
			lista.get(indiceAtomDepois).setNegar(true);
		}
		lista.remove(atomIndice.indice);
	}

	private void processar2(final List<Atom> lista, AtomIndice atomIndice, boolean negar, int indiceAtomAntes,
			int indiceAtomDepois) {
		if (!ehOperandoCalculavel(indiceAtomAntes, lista, negar) && variavel(indiceAtomDepois, lista)) {
			checkNegarERemover(lista, atomIndice, negar, indiceAtomDepois);
		} else if (!ehOperandoCalculavel(indiceAtomAntes, lista, negar) && param(indiceAtomDepois, lista)) {
			checkNegarERemover(lista, atomIndice, negar, indiceAtomDepois);
		} else if (!ehOperandoCalculavel(indiceAtomAntes, lista, negar) && invoke(indiceAtomDepois, lista)) {
			checkNegarERemover(lista, atomIndice, negar, indiceAtomDepois);
		} else {
			atomIndice.atom.setProcessado(true);
		}
	}

	static boolean variavel(int i, final List<Atom> lista) {
		if (i >= 0 && i < lista.size()) {
			Atom atom = lista.get(i);
			return atom.isVariavel();
		}
		return false;
	}

	static boolean param(int i, final List<Atom> lista) {
		if (i >= 0 && i < lista.size()) {
			Atom atom = lista.get(i);
			return atom.isParam();
		}
		return false;
	}

	private boolean invoke(int i, final List<Atom> lista) {
		if (i >= 0 && i < lista.size()) {
			Atom atom = lista.get(i);
			return atom.isStringAtom();
		}
		return false;
	}

	private void mergeAtom(int iantes, int depois, AtomIndice atomIndice, final List<Atom> lista)
			throws InstrucaoException {
		Atom antes = lista.get(iantes);
		Atom atom = lista.get(depois);
		Atom novo = merge(antes, atom);
		lista.set(depois, novo);
		lista.remove(atomIndice.indice);
	}

	private Atom merge(Atom antes, Atom atom) throws InstrucaoException {
		if (antes.getIndice() + 1 != atom.getIndice()) {
			return throwInstrucaoException();
		}
		if (atom.isBigInteger()) {
			return new Atom(antes.getValor() + atom.getValor(), Atom.BIG_INTEGER, atom.getIndice() - 1);
		} else if (atom.isBigDecimal()) {
			return new Atom(antes.getValor() + atom.getValor(), Atom.BIG_DECIMAL, atom.getIndice() - 1);
		}
		return throwInstrucaoException();
	}
}

class AtomIndice {
	final Atom atom;
	final int indice;

	public AtomIndice(Atom atom, int indice) {
		this.indice = indice;
		this.atom = atom;
	}
}