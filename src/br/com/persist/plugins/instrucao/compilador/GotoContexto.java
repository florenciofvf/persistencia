package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class GotoContexto extends Container {
	public static final String GOTO = "goto";
	int deslocamento;

	@Override
	public void indexar(Indexador indexador) {
		pontoDeslocamento = indexador.value();
		sequencia = indexador.get3();
	}

	@Override
	public void desviarImpl() throws InstrucaoException {
		IFContexto ifContexto = getIFContextoGoto();
		WhileContexto whileContexto = getWhileContextoGoto();
		if ((whileContexto == null && ifContexto == null) || (whileContexto != null && ifContexto != null)) {
			throw new InstrucaoException("erro.while_if_estrutura_invalida");
		}
		if (whileContexto != null) {
			deslocamento = whileContexto.getPontoDeslocamento();
			return;
		}
		Container container = ifContexto;
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
		print(pw, GOTO, "" + deslocamento);
	}
}