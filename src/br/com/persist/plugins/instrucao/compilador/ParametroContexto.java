package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;

public class ParametroContexto extends Container {
	public static final String LOAD_PARAM = "load_param";
	protected final String nome;
	protected final String head;
	protected final String tail;

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
	public void salvar(Compilador compilador, PrintWriter pw) {
		pw.println(InstrucaoConstantes.PREFIXO_PARAMETRO + nome);
	}

	@Override
	public String toString() {
		return nome;
	}
}