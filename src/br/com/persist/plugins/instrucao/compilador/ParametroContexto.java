package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public class ParametroContexto extends Container {
	public static final String LOAD_PARAM = "load_param";
	private final String depois;
	private final String antes;
	private final String nome;

	public ParametroContexto(Token token) {
		this.nome = token.getString();
		this.token = token;
		if (token.isLista()) {
			int pos = nome.indexOf(':');
			antes = nome.substring(1, pos);
			depois = nome.substring(pos + 1, nome.length() - 1);
		} else {
			depois = null;
			antes = null;
		}
	}

	public String getNome() {
		return nome;
	}

	public boolean contem(String string) {
		if (nome.equals(string)) {
			return true;
		}
		if (antes != null && depois != null) {
			return antes.equals(string) || depois.equals(string);
		}
		return false;
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