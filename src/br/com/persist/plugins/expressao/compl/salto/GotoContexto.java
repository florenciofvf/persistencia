package br.com.persist.plugins.expressao.compl.salto;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Indexador;

public class GotoContexto extends Contexto {
	public static final String GOTO = "goto";
	private boolean dispensavel;
	private Contexto destino;

	public Contexto getDestino() {
		return destino;
	}

	public void setDestino(Contexto destino) throws ExpressaoException {
		this.destino = destino;
		checkDestino();
	}

	private void checkDestino() throws ExpressaoException {
		if (destino == null) {
			throw new ExpressaoException("erro.goto.ponto_salto_nulo");
		}
	}

	public boolean isDispensavel() {
		return dispensavel;
	}

	public void setDispensavel(boolean dispensavel) {
		this.dispensavel = dispensavel;
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	public void listar(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get3();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		checkDestino();
		print(pw, GOTO, "" + destino.getIndice());
	}
}