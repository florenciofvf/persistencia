package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class InstrucaoGramatica {
	private final List<Metodo> metodos;
	private final List<Atom> atoms;
	private int indice;

	public InstrucaoGramatica(List<Atom> atoms) {
		metodos = new ArrayList<>();
		this.atoms = atoms;
		indice = 0;
	}

	private Metodo throwInstrucaoException() throws InstrucaoException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= indice; i++) {
			sb.append(atoms.get(i).getValor());
		}
		throw new InstrucaoException(sb.toString(), false);
	}

	public void processar(PrintWriter pw) throws InstrucaoException {
		Metodo metodo = getMetodo();
		while (metodo != null) {
			metodos.add(metodo);
			metodo = getMetodo();
		}
	}

	private Metodo getMetodo() throws InstrucaoException {
		if (indice >= atoms.size()) {
			return null;
		}
		Atom atom = atoms.get(0);
		if ("funcao_nativa".equals(atom.getValor())) {
			indice++;
			return criarMetodoNativo();
		} else if ("funcao".equals(atom.getValor())) {
			indice++;
			return criarMetodo();
		} else {
			throwInstrucaoException();
		}
	}

	private Atom getAtom() throws InstrucaoException {
		if (indice >= atoms.size()) {
			throwInstrucaoException();
		}
		return atoms.get(indice);
	}

	private Metodo criarMetodoNativo() throws InstrucaoException {
		Metodo resp = null;
		Atom atom = getAtom();
		if (atom.isStringAtom()) {
			if (atom.getValor().indexOf('.') != -1) {
				return throwInstrucaoException();
			}
			resp = new Metodo(atom.getValor());
			indice++;
		} else {
			return throwInstrucaoException();
		}
		List<Atom> parametros = getParametros();
		for (Atom a : parametros) {
			resp.addParam(new Param(a.getValor()));
		}
		return resp;
	}

	private List<Atom> getParametros() throws InstrucaoException {
		List<Atom> resp = new ArrayList<>();
		Atom atom = getAtom();
		if (!atom.isParenteseIni()) {
			throwInstrucaoException();
		} else {
			indice++;
		}
		boolean concluido = false;
		while (indice < atoms.size()) {
			atom = getAtom();
			if (atom.isParenteseFim()) {
				concluido = true;
				indice++;
				break;
			} else if (!atom.isVariavel()) {
				throwInstrucaoException();
			}
			resp.add(atom);
			indice++;
			atom = getAtom();
			if (atom.isVirgula()) {
				indice++;
				atom = getAtom();
				if (atom.isParenteseFim()) {
					throwInstrucaoException();
				}
			}
		}
		if (!concluido) {
			throwInstrucaoException();
		}
		return resp;
	}
}