package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class IFEqContexto extends Container {
	public static final String IF_EQ = "ifeq";
	int deslocamento;

	@Override
	public void indexar(Indexador indexador) {
		sequencia = indexador.get3();
	}

	@Override
	public void desviarImpl() throws InstrucaoException {
		IFContexto ifContexto = getIFContexto(this);
		if (ifContexto == null) {
			throw new InstrucaoException("erro.if_estrutura_invalida");
		}
		if (!ifContexto.isMinimo()) {
			deslocamento = ifContexto.getElse().getPontoDeslocamento();
			return;
		}
		CorpoContexto corpoContexto = getCorpoContexto(ifContexto);
		while (corpoContexto != null) {
			Container comando = corpoContexto.getContainerApos(ifContexto);
			if (comando != null) {
				deslocamento = comando.getPontoDeslocamento();
				break;
			}
			ifContexto = getIFContexto(corpoContexto);
			corpoContexto = getCorpoContexto(ifContexto);
		}
		if (deslocamento == 0) {
			throw new InstrucaoException("erro.ponto_deslocamento");
		}
	}

	@Override
	public void salvar(PrintWriter pw) {
		print(pw, IF_EQ, "" + deslocamento);
	}
}