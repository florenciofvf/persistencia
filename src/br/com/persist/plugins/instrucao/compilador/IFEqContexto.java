package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

public class IFEqContexto extends Container {
	public static final String IF_EQ = "ifeq";
	int deslocamento;

	@Override
	public void indexar(Indexador indexador) {
		sequencia = indexador.get3();
	}

	@Override
	public void desviarImpl() {
		IFContexto ifContexto = getIFContexto(this);
		if (ifContexto == null) {
			throw new IllegalStateException();
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
			throw new IllegalStateException("Sem ponto de deslocamento");
		}
	}

	@Override
	public void salvar(PrintWriter pw) {
		print(pw, IF_EQ, "" + deslocamento);
	}
}