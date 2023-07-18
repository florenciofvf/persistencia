package br.com.persist.plugins.instrucao.cmpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class InstrucaoGramatica {
	private final List<Atom> atoms;
	private int indice;

	public InstrucaoGramatica(List<Atom> atoms) {
		this.atoms = atoms;
		indice = 0;
	}

	private Metodo throwInstrucaoException(String... strings) throws InstrucaoException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= indice; i++) {
			sb.append(atoms.get(i).getValor());
		}
		if (strings != null) {
			for (String string : strings) {
				sb.append(" " + string);
			}
		}
		throw new InstrucaoException(sb.toString(), false);
	}

	public Biblio montarBiblio() throws InstrucaoException {
		Biblio biblio = new Biblio();
		Metodo metodo = getMetodo(biblio);
		while (metodo != null) {
			biblio.add(metodo);
			metodo = getMetodo(biblio);
		}
		biblio.montarMetodos();
		return biblio;
	}

	private Metodo getMetodo(Biblio biblio) throws InstrucaoException {
		if (indice >= atoms.size()) {
			return null;
		}
		Atom atom = getAtom();
		if (InstrucaoConstantes.FUNCAO_NATIVA.equals(atom.getValor())) {
			indice++;
			String biblioNativa = getBiblioNativa();
			Metodo metodo = criarMetodo(biblio);
			metodo.setBiblioNativa(biblioNativa);
			metodo.setNativo(true);
			return metodo;
		} else if (InstrucaoConstantes.FUNCAO.equals(atom.getValor())) {
			indice++;
			Metodo metodo = criarMetodo(biblio);
			for (Atom a : getAtoms()) {
				metodo.addAtom(a);
			}
			return metodo;
		} else if (InstrucaoConstantes.DEC_VARIAVEL.equals(atom.getValor())) {
			indice++;
			Atom atomNomeVar = getAtom();
			if (!atomNomeVar.isVariavel()) {
				return throwInstrucaoException();
			}
			indice++;
			Atom atomValor = getAtom();
			Atom atomPrefixo = null;
			if ("+".equals(atomValor.getValor()) || "-".equals(atomValor.getValor())) {
				atomPrefixo = atomValor;
				indice++;
				atomValor = getAtom();
			}
			if (!atomico(atomValor)) {
				return throwInstrucaoException();
			}
			checarPrefixo(atomPrefixo, atomValor);
			indice++;
			Metodo metodo = new Metodo(biblio, null);
			metodo.setAtomNomeVar(atomNomeVar);
			metodo.setAtomValorVar(mergear(atomPrefixo, atomValor));
			return metodo;
		} else {
			return throwInstrucaoException();
		}
	}

	private Atom mergear(Atom atomPrefixo, Atom atomValor) {
		if (atomPrefixo == null) {
			return atomValor;
		}
		atomValor.setLengthOffset(1);
		atomValor.setIndice(atomPrefixo.getIndice());
		return new Atom(atomPrefixo.getValor() + atomValor.getValor(), atomValor.getTipo(), atomPrefixo.getIndice());
	}

	private boolean atomico(Atom atom) {
		return atom.isBigInteger() || atom.isBigDecimal() || atom.isString();
	}

	private void checarPrefixo(Atom atomPrefixo, Atom atom) throws InstrucaoException {
		if (atomPrefixo != null && atomPrefixo.getValor().length() > 0 && atom.isString()) {
			throwInstrucaoException();
		}
		if (atomPrefixo != null && atomPrefixo.getIndice() + 1 != atom.getIndice()) {
			throwInstrucaoException();
		}
	}

	private Atom getAtom() throws InstrucaoException {
		if (indice >= atoms.size()) {
			throwInstrucaoException();
		}
		return atoms.get(indice);
	}

	private String getBiblioNativa() throws InstrucaoException {
		String resposta = null;
		Atom atom = getAtom();
		if (atom.isStringAtom()) {
			String classe = Util.replaceAll(atom.getValor(), "_", ".");
			if (classe.indexOf('.') == -1) {
				throwInstrucaoException("BiblioNativa");
			}
			resposta = classe;
			indice++;
		} else {
			throwInstrucaoException("BiblioNativa");
		}
		return resposta;
	}

	private Metodo criarMetodo(Biblio biblio) throws InstrucaoException {
		Metodo resp = null;
		Atom atom = getAtom();
		if (atom.isStringAtom()) {
			checarNomeMetodo(atom);
			resp = new Metodo(biblio, atom.getValor());
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
				if (atom.isParam()) {
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