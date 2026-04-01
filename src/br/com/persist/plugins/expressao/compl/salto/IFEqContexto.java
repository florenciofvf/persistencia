package br.com.persist.plugins.expressao.compl.salto;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Indexador;

public class IFEqContexto extends Contexto {
	public static final String IF_EQ = "ifeq";
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
			throw new ExpressaoException("erro.ifeq.ponto_salto_nulo");
		}
	}

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get3();
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
	public void salvar(PrintWriter pw) throws ExpressaoException {
		checkDestino();
		print(pw, IF_EQ, "" + destino.getIndice());
	}
}