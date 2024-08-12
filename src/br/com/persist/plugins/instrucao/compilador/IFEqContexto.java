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
			deslocamento = ifContexto.getElse().getInicioInstrucao();
			return;
		}
		CorpoContexto corpoContexto = getCorpoContexto(ifContexto);
		while (corpoContexto != null) {
			Container comando = corpoContexto.getComandoApos(ifContexto);
			if (comando != null) {
				deslocamento = comando.getInicioInstrucao();
				break;
			}
			ifContexto = getIFContexto(corpoContexto);
			if (ifContexto == null) {
				throw new IllegalStateException();
			}
			corpoContexto = getCorpoContexto(ifContexto);
		}
		if (deslocamento == 0) {
			throw new IllegalStateException("Sem deslocamento");
		}
	}

	@Override
	public void salvar(PrintWriter pw) {
		print(pw, IF_EQ, "" + deslocamento);
	}
}