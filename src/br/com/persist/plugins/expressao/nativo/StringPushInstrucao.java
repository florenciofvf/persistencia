package br.com.persist.plugins.expressao.nativo;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;
import br.com.persist.plugins.expressao.processador.Push;

public class StringPushInstrucao extends Instrucao implements Push {
	private String string;

	public StringPushInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, StringContexto.PUSH_STRING);
		if (parametros == null) {
			parametros = "";
		}
		this.string = normalizar(parametros);
	}

	public static String normalizar(String string) {
		if (string != null) {
			string = Util.replaceAll(string, "\\R", "\r");
			string = Util.replaceAll(string, "\\N", "\n");
			string = Util.replaceAll(string, "\\T", "\t");
		}
		return string;
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaOperando.push(string);
		log("[PUSH-STRING] ######### (string) ######### " + string, pilhaOperando);
	}

	@Override
	public String toString() {
		return super.toString() + " " + string;
	}
}