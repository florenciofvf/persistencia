package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ListaContexto extends Container {
	private final String id;

	public ListaContexto(Token token) {
		this.id = token.getString();
		this.token = token;
	}

	public String getId() {
		return id;
	}

	@Override
	public void indexar(Indexador indexador) {
		sequencia = indexador.get();
		indexarNegativo(indexador);
	}

	public static boolean ehListaVazia(String string) {
		return string != null && string.length() < 3;
	}

	@Override
	public void salvar(PrintWriter pw) throws InstrucaoException {
		if (ehListaVazia(id)) {
			print(pw, InvocacaoContexto.INVOKE_EXP, "ilist.create");
		} else {
			print(pw, ParametroContexto.LOAD_PARAM, id);
		}
	}

	@Override
	public String toString() {
		return id;
	}
}