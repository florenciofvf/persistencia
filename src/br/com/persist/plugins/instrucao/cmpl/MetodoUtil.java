package br.com.persist.plugins.instrucao.cmpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
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
		normalizarAtomicoLista(":");
		NoRaiz raiz = new NoRaiz();
		pilhaNo.push(raiz);
		int indice = 0;
		while (indice < atoms.size()) {
			Atom atom = getAtom(indice);
			if (atom.isHeadLista()) {
				pilhaNo.add(new LoadHeadLista(atom));
			} else if (atom.isTailLista()) {
				pilhaNo.add(new LoadTailLista(atom));
			} else if (atom.isStringAtom()) {
				indice++;
				indice += processoInvocacao(atom, indice);
			} else if (atom.isParenteseIni()) {
				processoExpressao(atom);
			} else if (atom.isListaVazia()) {
				pilhaNo.add(new LoadListaVazia());
			} else if (atom.isFuncaoInfixa()) {
				processoInfixa(atom);
			} else if (atom.isParenteseFim()) {
				pilhaNo.pop();
			} else if (ehTipoAtomico(atom)) {
				pilhaNo.add(new Push(atom));
			} else if (atom.isParam()) {
				indice += processoParam(atom, indice);
			} else if (atom.isVariavel()) {
				pilhaNo.add(new LoadVar(atom));
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

	private void normalizarAtomicoLista(String op) throws InstrucaoException {
		AtomIndice atomIndice = InstrucaoAtom.getAtomIndiceParaValor(atoms, op);
		while (atomIndice != null) {
			processar(atomIndice);
			atomIndice = InstrucaoAtom.getAtomIndiceParaValor(atoms, op);
		}
	}

	private void processar(AtomIndice atomIndice) throws InstrucaoException {
		int antes = atomIndice.indice - 1;
		int depois = atomIndice.indice + 1;
		if (isHead(antes)) {
			atoms.get(antes).setHeadLista(true);
			atoms.remove(atomIndice.indice);
		} else if (isTail(depois)) {
			atoms.get(depois).setTailLista(true);
			atoms.remove(atomIndice.indice);
		} else {
			throwInstrucaoException(atomIndice.indice);
		}
	}

	private boolean isHead(int antes) {
		return InstrucaoAtom.variavel(antes, atoms) || InstrucaoAtom.param(antes, atoms);
	}

	private boolean isTail(int depois) {
		return InstrucaoAtom.variavel(depois, atoms) || InstrucaoAtom.param(depois, atoms);
	}

	private int processoInvocacao(Atom atomInvoke, int indice) throws InstrucaoException {
		String nomeInvoke = atomInvoke.getValor();
		int incremento = 0;
		Atom atom = getAtom(indice);
		if (!atom.isParenteseIni()) {
			throwInstrucaoException(indice);
		}
		if (InstrucaoConstantes.IF.equals(nomeInvoke)) {
			If se = new If();
			pilhaNo.add(se);
			pilhaNo.push(se);
		} else if (InstrucaoConstantes.TAIL_CALL.equals(nomeInvoke)) {
			TailCall tailCall = new TailCall();
			pilhaNo.add(tailCall);
			pilhaNo.push(tailCall);
		} else if (InstrucaoConstantes.LAMBDA.equals(nomeInvoke)) {
			Lambda lambda = new Lambda();
			pilhaNo.add(lambda);
			pilhaNo.push(lambda);
		} else if (InstrucaoConstantes.VAR.equals(nomeInvoke)) {
			indice++;
			Atom atomVar = getAtom(indice);
			if (!atomVar.isVariavel()) {
				throwInstrucaoException(indice);
			}
			DeclareVar var = new DeclareVar(atomVar);
			pilhaNo.add(var);
			pilhaNo.push(var);
			incremento = 1;
		} else if (InstrucaoConstantes.VAL.equals(nomeInvoke)) {
			indice++;
			Atom atomVal = getAtom(indice);
			if (!atomVal.isVariavel()) {
				throwInstrucaoException(indice);
			}
			ModificVar val = new ModificVar(atomVal);
			pilhaNo.add(val);
			pilhaNo.push(val);
			incremento = 1;
		} else {
			Invoke invoke = new Invoke(atomInvoke);
			pilhaNo.add(invoke);
			pilhaNo.push(invoke);
		}
		return incremento;
	}

	private int processoParam(Atom atomParam, int indice) throws InstrucaoException {
		indice++;
		int incremento = 0;
		if (indice < atoms.size()) {
			Atom a = getAtom(indice);
			if (a.isParenteseIni()) {
				InvokeDin invoke = new InvokeDin(atomParam);
				pilhaNo.add(invoke);
				pilhaNo.push(invoke);
				incremento = 1;
			} else {
				pilhaNo.add(new LoadPar(atomParam));
			}
		} else {
			pilhaNo.add(new LoadPar(atomParam));
		}
		return incremento;
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
			No ultimoNo = noAtivado.excluirUltimoNo();
			novaInfixa.add(ultimoNo);
			noAtivado.add(novaInfixa);
		}
		pilhaNo.push(novaInfixa);
	}

	private void processoVirgula(int indice) throws InstrucaoException {
		No no = pilhaNo.ref();
		if ((no instanceof Invoke) || (no instanceof InvokeDin) || (no instanceof If) || (no instanceof DeclareVar)
				|| (no instanceof ModificVar) || (no instanceof TailCall) || (no instanceof Lambda)) {
			return;
		}
		throwInstrucaoException(indice);
	}

	private static boolean ehTipoAtomico(Atom atom) {
		return atom.isString() || atom.isBigInteger() || atom.isBigDecimal();
	}

	static {
		infixas.put("+", new Somar());
		infixas.put("@", new AddLista());
		infixas.put("-", new Subtrair());
		infixas.put("*", new Multiplicar());
		infixas.put("**", new Pow());
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