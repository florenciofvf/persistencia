package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class Biblio {
	private final Map<String, Atom> variaveis;
	private final List<Metodo> metodos;

	public Biblio() {
		variaveis = new LinkedHashMap<>();
		metodos = new ArrayList<>();
	}

	public void add(Metodo metodo) {
		if (metodo.getNome() == null) {
			addVariavel(metodo);
		} else {
			metodos.add(metodo);
		}
	}

	private void addVariavel(Metodo metodo) {
		String name = metodo.getAtomNomeVar().getValor();
		variaveis.put(name, metodo.getAtomValorVar());
	}

	void montarMetodos() throws InstrucaoException {
		for (Metodo met : metodos) {
			met.montarEstrutura();
		}
		for (Metodo met : metodos) {
			met.finalizar();
		}
	}

	boolean isEmpty() {
		return variaveis.isEmpty() && metodos.isEmpty();
	}

	void print(PrintWriter pw) throws InstrucaoException {
		for (Map.Entry<String, Atom> entry : variaveis.entrySet()) {
			String nomeVar = entry.getKey();
			Atom atomValor = entry.getValue();
			pw.print(InstrucaoConstantes.PREFIXO_VAR + nomeVar + " ");
			pw.println(getTipo(atomValor) + "&" + atomValor.getValor());
		}
		for (Metodo metodo : metodos) {
			pw.println();
			metodo.print(pw);
		}
	}

	private String getTipo(Atom atom) {
		if (atom.isString()) {
			return InstrucaoConstantes.STRING;
		} else if (atom.isBigInteger()) {
			return InstrucaoConstantes.BIG_INTEGER;
		} else if (atom.isBigDecimal()) {
			return InstrucaoConstantes.BIG_DECIMAL;
		}
		return null;
	}
}