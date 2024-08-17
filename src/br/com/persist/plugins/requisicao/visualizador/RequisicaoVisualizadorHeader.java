package br.com.persist.plugins.requisicao.visualizador;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Util;
import br.com.persist.data.Objeto;
import br.com.persist.data.Texto;
import br.com.persist.data.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoConstantes;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public abstract class RequisicaoVisualizadorHeader extends AbstratoRequisicaoVisualizador {
	protected String getAccessToken(Tipo tipo) {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoAccessToken = objeto.getValor("access_token");
			return tipoAccessToken instanceof Texto ? tipoAccessToken.toString() : null;
		}
		return null;
	}

	public static void setAccesToken(String accessToken) throws ArgumentoException {
		if (!Util.isEmpty(accessToken)) {
			Variavel vAccessToken = VariavelProvedor.getVariavel(RequisicaoConstantes.VAR_ACCESS_TOKEN);
			if (vAccessToken == null) {
				VariavelProvedor.adicionar(RequisicaoConstantes.VAR_ACCESS_TOKEN, accessToken);
			} else {
				vAccessToken.setValor(accessToken);
			}
		}
	}

	protected void setVarAuthToken(String varAuthToken, String string) throws ArgumentoException {
		if (Util.isEmpty(varAuthToken) || Util.isEmpty(string)) {
			return;
		}
		String str = "name=\"authenticity_token\" value=\"";
		int pos = string.indexOf(str);
		if (pos == -1) {
			return;
		}
		int pos2 = string.indexOf('"', pos + str.length() + 1);
		String valor = string.substring(pos + str.length(), pos2);
		Variavel var = VariavelProvedor.getVariavel(varAuthToken);
		if (var == null) {
			VariavelProvedor.adicionar(varAuthToken, valor);
		} else {
			var.setValor(valor);
		}
	}

	public static void setVarCookie(String varCookie, String cookie) throws ArgumentoException {
		if (Util.isEmpty(varCookie) || Util.isEmpty(cookie)) {
			return;
		}
		Variavel var = VariavelProvedor.getVariavel(varCookie);
		if (var == null) {
			VariavelProvedor.adicionar(varCookie, cookie);
		} else {
			var.setValor(cookie);
		}
	}
}