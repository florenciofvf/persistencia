package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class Metodo {
	protected final Return retorn = new Return();
	private final List<Metodo> metodosLambdas;
	private final List<Lambda> lambdas;
	private final List<No> parametros;
	private final List<Atom> atoms;
	private String biblioNativa;
	private final Biblio biblio;
	private final String nome;
	private Atom atomValorVar;
	private Atom atomNomeVar;
	private boolean nativo;
	private No no;

	public Metodo(Biblio biblio, String nome) {
		metodosLambdas = new ArrayList<>();
		parametros = new ArrayList<>();
		lambdas = new ArrayList<>();
		atoms = new ArrayList<>();
		this.biblio = biblio;
		this.nome = nome;
	}

	void registrarLambda(Lambda lambda) {
		lambdas.add(lambda);
	}

	public Biblio getBiblio() {
		return biblio;
	}

	public No getNo() {
		return no;
	}

	public void setNo(No no) {
		this.no = no;
	}

	public List<No> getParametros() {
		return parametros;
	}

	public void addParam(No no) {
		if (no instanceof Param) {
			parametros.add(no);
		}
	}

	public Atom getAtomValorVar() {
		return atomValorVar;
	}

	public void setAtomValorVar(Atom atomValorVar) {
		this.atomValorVar = atomValorVar;
	}

	public Atom getAtomNomeVar() {
		return atomNomeVar;
	}

	public void setAtomNomeVar(Atom atomNomeVar) {
		this.atomNomeVar = atomNomeVar;
	}

	public String getNome() {
		return nome;
	}

	public boolean isNativo() {
		return nativo;
	}

	public void setNativo(boolean nativo) {
		this.nativo = nativo;
	}

	public String getBiblioNativa() {
		return biblioNativa;
	}

	public void setBiblioNativa(String biblioNativa) {
		this.biblioNativa = biblioNativa;
	}

	public List<Atom> getAtoms() {
		return atoms;
	}

	public void addAtom(Atom atom) {
		if (atom != null) {
			atom.setProcessado(false);
			atoms.add(atom);
		}
	}

	void montarEstrutura() throws InstrucaoException {
		if (nativo) {
			return;
		}
		MetodoUtil util = new MetodoUtil(this);
		no = util.montar();
	}

	void finalizar() throws InstrucaoException {
		if (nativo) {
			return;
		}
		no.normalizarEstrutura(this);
		retorn.normalizarEstrutura(this);
		AtomicInteger atomic = new AtomicInteger(0);
		no.indexar(atomic);
		retorn.indexar(atomic);
		no.configurarDesvio();
		retorn.configurarDesvio();
		processarLambdas();
	}

	private void processarLambdas() throws InstrucaoException {
		for (Lambda lambda : lambdas) {
			Metodo metodo = new Metodo(biblio, lambda.getNomeFinal());
			for (No param : lambda.getParametros()) {
				metodo.addParam(param);
			}
			metodo.no = lambda.getNoRaiz();
			metodosLambdas.add(metodo);
		}
		for (Metodo met : metodosLambdas) {
			met.finalizar();
		}
	}

	public void print(PrintWriter pw) throws InstrucaoException {
		String prefixo = nativo ? InstrucaoConstantes.PREFIXO_METODO_NATIVO : InstrucaoConstantes.PREFIXO_METODO;
		pw.println(prefixo + (nativo ? biblioNativa + " " : "") + nome);
		for (No n : parametros) {
			n.print(pw);
		}
		if (nativo) {
			return;
		}
		no.print(pw);
		retorn.print(pw);
		printLambdas(pw);
	}

	private void printLambdas(PrintWriter pw) throws InstrucaoException {
		for (Metodo metodo : metodosLambdas) {
			pw.println();
			metodo.print(pw);
		}
	}

	public Return getReturn() {
		return retorn;
	}

	@Override
	public String toString() {
		return nome + "(" + parametros + ")";
	}
}