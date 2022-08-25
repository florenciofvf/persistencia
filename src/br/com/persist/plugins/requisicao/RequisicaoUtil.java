package br.com.persist.plugins.requisicao;

import br.com.persist.parser.Logico;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;

public class RequisicaoUtil {

	private RequisicaoUtil() {
	}

	public static boolean getAutoSaveVar(Tipo parametros) {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipo = objeto.getValor("AutoSaveVar");
			String string = tipo instanceof Logico ? tipo.toString() : null;
			return Boolean.parseBoolean(string);
		}
		return false;
	}

	public static String getAtributoVarAuthToken(Tipo parametros) {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipo = objeto.getValor("SetVarAuthToken");
			return tipo instanceof Texto ? tipo.toString() : null;
		}
		return null;
	}

	public static String getAtributoVarCookie(Tipo parametros) {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipo = objeto.getValor("SetVarCookie");
			return tipo instanceof Texto ? tipo.toString() : null;
		}
		return null;
	}
}