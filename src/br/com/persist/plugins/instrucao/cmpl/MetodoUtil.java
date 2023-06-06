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

	No criar() throws InstrucaoException {
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
		Invocacao invocacao = new Invocacao(metodo);
		pilhaNo.peek().add(invocacao);
		pilhaNo.push(invocacao);
		indice++;
	}

	private void processoExpressao(Atom atom) {
		Expressao expressao = new Expressao(atom.isNegarExpressao());
		pilhaNo.peek().add(expressao);
		pilhaNo.push(expressao);
		indice++;
	}

	private void processoVariavel(Atom atom) {
		Variavel variavel = new Variavel(atom.getValor(), atom.isNegarVariavel());
		pilhaNo.peek().add(variavel);
		indice++;
	}

	private void processoVirgula() throws InstrucaoException {
		if (!(pilhaNo.peek() instanceof Invocacao)) {
			throwInstrucaoException();
		}
		indice++;
	}

	private void processoAtomico(Atom atom) {
		Atomico atomico = new Atomico(atom.getValor());
		pilhaNo.peek().add(atomico);
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
		return atom.isVariavel() || atom.isString() || atom.isBigInteger() || atom.isBigDecimal();
	}

	static {
		// infixas.put("+", Somar.class);
		// infixas.put("-", Subtrair.class);
		// infixas.put("*", Multiplicar.class);
		// infixas.put("/", Dividir.class);
		// infixas.put("%", Resto.class);
		//
		// infixas.put("==", Igual.class);
		// // infixas.put("!=", Diferente.class);
		// infixas.put("<", Menor.class);
		// infixas.put("<=", MenorIgual.class);
		// infixas.put(">", Maior.class);
		// infixas.put(">=", MaiorIgual.class);
		//
		// infixas.put("^", Oux.class);
		// infixas.put("&&", E.class);
		// infixas.put("||", Ou.class);
	}
}