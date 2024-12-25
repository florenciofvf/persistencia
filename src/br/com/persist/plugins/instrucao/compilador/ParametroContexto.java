package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public class ParametroContexto extends Container {
	public static final String LOAD_PARAM = "load_param";
	private final String nome;
	private final String head;
	private final String tail;

	public ParametroContexto(Token token) {
		this.nome = token.getString();
		this.token = token;
		if (token.isLista()) {
			int pos = nome.indexOf(':');
			head = nome.substring(1, pos);
			tail = nome.substring(pos + 1, nome.length() - 1);
		} else {
			head = null;
			tail = null;
		}
	}

	public String getNome() {
		return nome;
	}

	public boolean contem(String string) {
		if (nome.equals(string)) {
			return true;
		}
		if (head != null && tail != null) {
			return head.equals(string) || tail.equals(string);
		}
		return false;
	}

	public boolean isHead(String string) {
		return head != null && head.equals(string);
	}

	public boolean isTail(String string) {
		return tail != null && tail.equals(string);
	}

	@Override
	public void filtroConstParam(List<Token> coletor) {
		coletor.add(token.novo(Tipo.PARAMETRO));
	}

	@Override
	public void salvar(PrintWriter pw) {
		pw.println(InstrucaoConstantes.PREFIXO_PARAMETRO + nome);
	}

	@Override
	public String toString() {
		return nome;
	}
}