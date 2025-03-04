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
		Container container = ifContexto != null ? ifContexto : whileContexto;
		CorpoContexto corpoParent = getCorpoContexto(container);
		while (corpoParent != null) {
			Container comando = corpoParent.getContainerApos(container);
			if (comando != null) {
				deslocamento = comando.getPontoDeslocamento();
				break;
			}
			container = corpoParent.pai;
			if (container instanceof ElseContexto) {
				container = container.pai;
			}
			corpoParent = getCorpoContexto(container);
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