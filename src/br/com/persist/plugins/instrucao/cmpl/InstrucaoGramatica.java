package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
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
		for (Metodo met : metodos) {
			met.criarHierarquia();
		}
	}

	private Metodo getMetodo() throws InstrucaoException {
		if (indice >= atoms.size()) {
			return null;
		}
		Atom atom = getAtom();
		if ("funcao_nativa".equals(atom.getValor())) {
			indice++;
			Metodo metodo = criarMetodo();
			metodo.setNativo(true);
			return metodo;
		} else if ("funcao".equals(atom.getValor())) {
			indice++;
			Metodo metodo = criarMetodo();
			metodo.setAtoms(getAtoms());
			return metodo;
		} else {
			return throwInstrucaoException();
		}
	}

	private Atom getAtom() throws InstrucaoException {
		if (indice >= atoms.size()) {
			throwInstrucaoException();
		}
		return atoms.get(indice);
	}

	private Metodo criarMetodo() throws InstrucaoException {
		Metodo resp = null;
		Atom atom = getAtom();
		if (atom.isStringAtom()) {
			checarNomeMetodo(atom);
			resp = new Metodo(atom.getValor());
			indice++;
		} else {
			return throwInstrucaoException();
		}
		setParametros(resp);
		return resp;
	}

	private void checarNomeMetodo(Atom atom) throws InstrucaoException {
		if (atom.getValor().indexOf('.') != -1) {
			throwInstrucaoException();
		}
	}

	private void setParametros(Metodo resp) throws InstrucaoException {
		List<Atom> parametros = getParametros();
		for (Atom atom : parametros) {
			resp.addParam(new Param(atom.getValor()));
		}
	}

	private List<Atom> getParametros() throws InstrucaoException {
		List<Atom> resp = new ArrayList<>();
		Atom atom = getAtom();
		if (!atom.isParenteseIni()) {
			throwInstrucaoException();
		} else {
			resp.add(atom);
			indice++;
		}
		while (indice < atoms.size()) {
			atom = getAtom();
			resp.add(atom);
			indice++;
			if (atom.isParenteseFim()) {
				break;
			}
		}
		checarFechamentoParam(resp);
		resp.remove(0);
		resp.remove(resp.size() - 1);
		filtrarParam(resp);
		return resp;
	}

	private void checarFechamentoParam(List<Atom> atoms) throws InstrucaoException {
		Atom ini = atoms.get(0);
		Atom fim = atoms.get(atoms.size() - 1);
		if (!ini.isParenteseIni() || !fim.isParenteseFim()) {
			throwInstrucaoException();
		}
	}

	private void filtrarParam(List<Atom> atoms) throws InstrucaoException {
		Iterator<Atom> it = atoms.iterator();
		boolean parametro = true;
		while (it.hasNext()) {
			Atom atom = it.next();
			if (parametro) {
				if (atom.isVariavel()) {
					parametro = false;
				} else {
					throwInstrucaoException();
				}
			} else {
				if (atom.isVirgula()) {
					parametro = true;
					it.remove();
				} else {
					throwInstrucaoException();
				}
			}
		}
	}

	private List<Atom> getAtoms() throws InstrucaoException {
		List<Atom> resp = new ArrayList<>();
		Atom atom = getAtom();
		if (!atom.isChaveIni()) {
			throwInstrucaoException();
		} else {
			resp.add(atom);
			indice++;
		}
		while (indice < atoms.size()) {
			atom = getAtom();
			resp.add(atom);
			indice++;
			if (atom.isChaveFim()) {
				break;
			}
		}
		checarFechamentoAtom(resp);
		resp.remove(0);
		resp.remove(resp.size() - 1);
		return resp;
	}

	private void checarFechamentoAtom(List<Atom> atoms) throws InstrucaoException {
		Atom ini = atoms.get(0);
		Atom fim = atoms.get(atoms.size() - 1);
		if (!ini.isChaveIni() || !fim.isChaveFim()) {
			throwInstrucaoException();
		}
	}
}