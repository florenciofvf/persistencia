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

	MetodoUtil(Metodo metodo) {
		this.metodo = Objects.requireNonNull(metodo);
		atoms = metodo.getAtoms();
	}

	private No throwInstrucaoException(int indice) throws InstrucaoException {
		StringBuilder sb = new StringBuilder(metodo.toString() + Constantes.QL);
		for (int i = 0; i <= indice; i++) {
			sb.append(atoms.get(i).getValor());
		}
		throw new InstrucaoException(sb.toString(), false);
	}

	private Atom getAtom(int indice) throws InstrucaoException {
		if (indice >= atoms.size()) {
			throwInstrucaoException(indice);
		}
		return atoms.get(indice);
	}

	No montar() throws InstrucaoException {
		NoRaiz raiz = new NoRaiz();
		pilhaNo.push(raiz);
		int indice = 0;
		while (indice < atoms.size()) {
			Atom atom = getAtom(indice);
			if (atom.isStringAtom()) {
				indice++;
				processoInvocacao(atom.getValor(), indice);
			} else if (atom.isParenteseIni()) {
				processoExpressao(atom);
			} else if (atom.isFuncaoInfixa()) {
				processoInfixa(atom);
			} else if (atom.isParenteseFim()) {
				pilhaNo.pop();
			} else if (ehTipoAtomico(atom)) {
				pilhaNo.add(new Push(atom));
			} else if (atom.isVariavel()) {
				pilhaNo.add(new Load(atom));
			} else if (atom.isVirgula()) {
				processoVirgula(indice);
			} else {
				throwInstrucaoException(indice);
			}
			indice++;
		}
		if (raiz.getNos().size() != 1) {
			throw new InstrucaoException(metodo.toString() + " <<< CHEQUE O COMANDO DE RETORNO", false);
		}
		return raiz.excluirUltimoNo();
	}

	private void processoInvocacao(String metodo, int indice) throws InstrucaoException {
		Atom atom = getAtom(indice);
		if (!atom.isParenteseIni()) {
			throwInstrucaoException(indice);
		}
		if ("if".equals(metodo)) {
			If se = new If();
			pilhaNo.add(se);
			pilhaNo.push(se);
		} else {
			Invoke invoke = new Invoke(metodo);
			pilhaNo.add(invoke);
			pilhaNo.push(invoke);
		}
	}

	private void processoExpressao(Atom atom) throws InstrucaoException {
		Expression expression = new Expression(atom);
		pilhaNo.add(expression);
		pilhaNo.push(expression);
	}

	private void processoInfixa(Atom atom) throws InstrucaoException {
		Infixa novaInfixa = infixas.get(atom.getValor()).clonar();
		No noAtivado = pilhaNo.ref();
		if (novaInfixa.possuoPrioridadeSobre(noAtivado.getUltimoNo())) {
			Infixa infixa = (Infixa) noAtivado.getUltimoNo();
			No operandoDireito = infixa.excluirUltimoNo();
			novaInfixa.add(operandoDireito);
			infixa.add(novaInfixa);
		} else {
			No ultimoParam = noAtivado.excluirUltimoNo();
			novaInfixa.add(ultimoParam);
			noAtivado.add(novaInfixa);
		}
		pilhaNo.push(novaInfixa);
	}

	private void processoVirgula(int indice) throws InstrucaoException {
		No no = pilhaNo.ref();
		if ((no instanceof Invoke) || (no instanceof If)) {
			return;
		}
		throwInstrucaoException(indice);
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
		infixas.put("<=", new MenorIgual());
		infixas.put(">=", new MaiorIgual());
		infixas.put("<", new Menor());
		infixas.put(">", new Maior());

		infixas.put("&&", new And());
		infixas.put("||", new Or());
		infixas.put("^", new Xor());
	}
}