package br.com.persist.plugins.expressao.compl.invocacao;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Token;

public class InvocacaoContexto extends Contexto {
	public static final String INVOKE_RETR = "invoke_retr";
	public static final String INVOKE_VOID = "invoke_void";
	private boolean comRetorno;
	private String biblio;
	private String metodo;

	public InvocacaoContexto(Token token, boolean comRetorno) {
		super(token);
		this.comRetorno = comRetorno;
	}

	@Context("invocar_funcao")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}

	@Override
	protected void empilharLocalPos(List<Contexto> lista) {
		lista.add(this);
		empilharLocalNegativo(lista);
	}

	@Override
	protected void listarPos(List<Contexto> lista) {
		lista.add(this);
		listarNegativo(lista);
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		if (comRetorno) {
			print(pw, INVOKE_RETR, biblio, metodo);
		} else {
			print(pw, INVOKE_VOID, biblio, metodo);
		}
	}
}