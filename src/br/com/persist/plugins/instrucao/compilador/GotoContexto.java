package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class GotoContexto extends Container {
	public static final String GOTO = "goto";
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
			throw new InstrucaoException("Sem ponto de deslocamento", false);
		}
	}

	@Override
	public void salvar(PrintWriter pw) {
		print(pw, GOTO, "" + deslocamento);
	}
}