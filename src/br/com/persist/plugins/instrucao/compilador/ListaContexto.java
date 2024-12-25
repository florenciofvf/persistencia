package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public class ListaContexto extends Container {
	private Token tokenIdentity;
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

	@Override
	public void filtroConstParam(List<Token> coletor) {
		coletor.add(tokenIdentity);
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
		tokenIdentity = token.novo(Tipo.PARAMETRO);
	}

	@Override
	public String toString() {
		return id;
	}
}