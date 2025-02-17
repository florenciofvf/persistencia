package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class ImportaContexto extends Container {
	private String string;
	private String alias;

	public ImportaContexto() {
		contexto = Contextos.TEXTO;
	}

	public String getString() {
		return string;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		contexto.string(compilador, token);
		if (string == null) {
			string = token.getString();
		} else if (alias == null) {
			alias = token.getString();
			contexto = Contextos.PONTO_VIRGULA;
		}
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		pw.println(InstrucaoConstantes.PREFIXO_IMPORT + string + InstrucaoConstantes.ESPACO + alias);
	}

	@Override
	public String toString() {
		return string + InstrucaoConstantes.ESPACO + alias;
	}
}