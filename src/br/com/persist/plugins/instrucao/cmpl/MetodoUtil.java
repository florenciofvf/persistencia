package br.com.persist.plugins.instrucao.cmpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

class MetodoUtil {
	static final Map<String, Infixa> infixas = new HashMap<>();
	private final PilhaNo pilhaNo = new PilhaNo();
	private final List<Atom> atoms;
	private final Metodo metodo;
	private int indice;

	MetodoUtil(Metodo metodo) {
		this.metodo = Objects.requireNonNull(metodo);
		atoms = metodo.getAtoms();
		indice = 0;
	}

	private No throwInstrucaoException() throws InstrucaoException {
		StringBuilder sb = new StringBuilder(metodo.toString() + Constantes.QL);
		for (int i = 0; i <= indice; i++) {
			sb.append(atoms.get(i).getValor());
		}
		throw new InstrucaoException(sb.toString(), false);
	}

	private Atom getAtom() throws InstrucaoException {
		if (indice >= atoms.size()) {
			throwInstrucaoException();
		}
		return atoms.get(indice);
	}

	No montar() throws InstrucaoException {
		NoRaiz raiz = new NoRaiz();
		pilhaNo.push(raiz);
		while (indice < atoms.size()) {
			Atom atom = getAtom();
			if (atom.isStringAtom()) {
				indice++;
				processoInvocacao(atom.getValor());
			} else if (atom.isParenteseIni()) {
				processoExpressao(atom);
			} else if (atom.isParenteseFim()) {
				pilhaNo.pop();
				while (pilhaNo.peek() instanceof Infixa) {
					pilhaNo.pop();
				}
			} else if (atom.isVariavel()) {
				processoVariavel(atom);
			} else if (atom.isVirgula()) {
				processoVirgula();
			} else if (ehTipoAtomico(atom)) {
				processoAtomico(atom);
			} else if (atom.isFuncaoInfixa()) {
				processoInfixa(atom);
			} else {
				throwInstrucaoException();
			}
		}
		if (raiz.getNos().size() != 1) {
			throw new InstrucaoException(metodo.toString() + " <<< CHEQUE O COMANDO DE RETORNO", false);
		}
		return raiz.get(0);
	}

	private void processoInvocacao(String metodo) throws InstrucaoException {
		Atom atom = getAtom();
		if (!atom.isParenteseIni()) {
			throwInstrucaoException();
		}
		if ("if".equals(metodo)) {
			If se = new If();
			pilhaNo.peek().add(se);
			pilhaNo.push(se);
		} else {
			Invoke invoke = new Invoke(metodo);
			pilhaNo.peek().add(invoke);
			pilhaNo.push(invoke);
		}
		indice++;
	}

	private void processoExpressao(Atom atom) {
		Expressao expressao = new Expressao(atom.isNegarExpressao());
		pilhaNo.peek().add(expressao);
		pilhaNo.push(expressao);
		indice++;
	}

	private void processoVariavel(Atom atom) {
		Load load = new Load(atom);
		pilhaNo.peek().add(load);
		indice++;
	}

	private void processoVirgula() throws InstrucaoException {
		if (pilhaNo.peek() instanceof Invoke) {
			indice++;
			return;
		}
		if (pilhaNo.peek() instanceof If) {
			indice++;
			return;
		}
		throwInstrucaoException();
	}

	private void processoAtomico(Atom atom) {
		Push push = new Push(atom);
		pilhaNo.peek().add(push);
		indice++;
	}

	private void processoInfixa(Atom atom) {
		Infixa novaInfixa = infixas.get(atom.getValor()).clonar();

		No sel = pilhaNo.peek();
		if (novaInfixa.possuoPrioridadeSobre(sel.getUltimoNo())) {
			Infixa ultimaInfixa = (Infixa) sel.getUltimoNo();
			No no = ultimaInfixa.excluirUltimoNo();
			novaInfixa.add(no);
			ultimaInfixa.add(novaInfixa);
		} else {
			No no = sel.excluirUltimoNo();
			novaInfixa.add(no);
			sel.add(novaInfixa);
		}

		pilhaNo.push(novaInfixa);
		indice++;
	}

	private static boolean ehTipoAtomico(Atom atom) {
		return atom.isString() || atom.isBigInteger() || atom.isBigDecimal();
	}

	static {
		infixas.put("+", new Somar());
		infixas.put("-", new Subtrair());
		infixas.put("*", new Multiplicar());
		infixas.put("/", new Dividir());
		infixas.put("%", new Resto());

		infixas.put("==", new Igual());
		infixas.put("!=", new Diferente());
		infixas.put("<", new Menor());
		infixas.put("<=", new MenorIgual());
		infixas.put(">", new Maior());
		infixas.put(">=", new MaiorIgual());

		infixas.put("^", new Oux());
		infixas.put("&&", new And());
		infixas.put("||", new Ou());
	}
}