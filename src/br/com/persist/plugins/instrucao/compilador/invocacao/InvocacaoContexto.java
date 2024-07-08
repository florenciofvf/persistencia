package br.com.persist.plugins.instrucao.compilador.invocacao;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class InvocacaoContexto extends Container {
	private final InvocacaoArgumentoContexto argumento;
	private final char[] modoPai;
	private final String nome;

	public InvocacaoContexto(char[] modoPai, String nome) {
		argumento = new InvocacaoArgumentoContexto();
		this.modoPai = modoPai;
		this.nome = nome;
	}

	public InvocacaoArgumentoContexto getArgumento() {
		return argumento;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (";".equals(token.getString())) {
			compilador.setContexto(getPai());
			getPai().setModo(modoPai);
		} else {
			compilador.invalidar(token);
		}
	}
}