package br.com.persist.plugins.requisicao.visualizador;

import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoConstantes;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public abstract class RequisicaoVisualizadorHeader extends AbstratoRequisicaoVisualizador
		implements RequisicaoVisualizador {

	protected String getAccessToken(Tipo tipo) {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoAccessToken = objeto.getValor("access_token");
			return tipoAccessToken instanceof Texto ? tipoAccessToken.toString() : null;
		}
		return null;
	}

	public static void setAccesToken(String accessToken) {
		if (!Util.estaVazio(accessToken)) {
			Variavel vAccessToken = VariavelProvedor.getVariavel(RequisicaoConstantes.VAR_ACCESS_TOKEN);
			if (vAccessToken == null) {
				vAccessToken = new Variavel(RequisicaoConstantes.VAR_ACCESS_TOKEN, accessToken);
				VariavelProvedor.adicionar(vAccessToken);
			} else {
				vAccessToken.setValor(accessToken);
			}
		}
	}

	protected void setVarAuthToken(String string, String varAuthToken) {
		if (Util.estaVazio(string) || Util.estaVazio(varAuthToken)) {
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
			var = new Variavel(varAuthToken, valor);
			VariavelProvedor.adicionar(var);
		} else {
			var.setValor(valor);
		}
	}

	public static void setVarCookie(Map<String, List<String>> map, String varCookie) {
		if (map == null || Util.estaVazio(varCookie)) {
			return;
		}
		List<String> lista = map.get("Set-Cookie");
		if (lista != null && !lista.isEmpty()) {
			String valor = lista.get(0);
			Variavel var = VariavelProvedor.getVariavel(varCookie);
			if (var == null) {
				var = new Variavel(varCookie, valor);
				VariavelProvedor.adicionar(var);
			} else {
				var.setValor(valor);
			}
		}
	}
}