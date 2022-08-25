package br.com.persist.plugins.requisicao;

import br.com.persist.parser.Logico;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;

public class RequisicaoUtil {

	private RequisicaoUtil() {
	}

	public static boolean getAutoSaveVar(Tipo tipo) {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoAuto = objeto.getValor("AutoSaveVar");
			String string = tipoAuto instanceof Logico ? tipoAuto.toString() : null;
			return Boolean.parseBoolean(string);
		}
		return false;
	}

	public static String getAtributoVarAuthToken(Tipo tipo) {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoAuth = objeto.getValor("SetVarAuthToken");
			return tipoAuth instanceof Texto ? tipoAuth.toString() : null;
		}
		return null;
	}

	public static String getAtributoVarCookie(Tipo parametros) {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipoCook = objeto.getValor("SetVarCookie");
			return tipoCook instanceof Texto ? tipoCook.toString() : null;
		}
		return null;
	}
}