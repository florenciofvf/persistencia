package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class IFEqContexto extends Container {
	public static final String IF_EQ = "ifeq";
	int deslocamento;

	@Override
	public void indexar(Indexador indexador) {
		pontoDeslocamento = indexador.value();
		sequencia = indexador.get3();
	}

	@Override
	public void desviarImpl() throws InstrucaoException {
		IFContexto ifContexto = getIFContextoIFEq();
		WhileContexto whileContexto = getWhileContextoIFEq();
		if ((whileContexto == null && ifContexto == null) || (whileContexto != null && ifContexto != null)) {
			throw new InstrucaoException("erro.while_if_estrutura_invalida");
		}
		if (ifContexto != null && !ifContexto.isMinimo()) {
			deslocamento = ifContexto.getElse().getPontoDeslocamento();
			if (deslocamento == 0) {
				throw new InstrucaoException("erro.ponto_deslocamento");
			}
			return;
		}
		Container parent = ifContexto != null ? ifContexto : whileContexto;
		CorpoContexto corpoParent = getCorpoContexto(parent);
		while (corpoParent != null) {
			Container comando = corpoParent.getContainerApos(parent);
			if (comando != null) {
				deslocamento = comando.getPontoDeslocamento();
				break;
			}
			parent = corpoParent.pai;
			corpoParent = getCorpoContexto(parent);
		}
		if (deslocamento == 0) {
			throw new InstrucaoException("erro.ponto_deslocamento");
		}
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) {
		print(pw, IF_EQ, "" + deslocamento);
	}
}