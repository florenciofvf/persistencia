package br.com.persist.plugins.requisicao.conteudo;

import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoConstantes;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public abstract class RequisicaoHeader implements RequisicaoConteudo {

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

	protected void setAuthToken(String string) {
		if (!Util.estaVazio(string)) {
			String str = "name=\"authenticity_token\" value=\"";
			int pos = string.indexOf(str);
			if (pos == -1) {
				return;
			}
			int pos2 = string.indexOf('"', pos + str.length() + 1);
			String valor = string.substring(pos + str.length(), pos2);
			Variavel vAuthToken = VariavelProvedor.getVariavel(RequisicaoConstantes.VAR_AUTH_TOKEN);
			if (vAuthToken == null) {
				vAuthToken = new Variavel(RequisicaoConstantes.VAR_AUTH_TOKEN, valor);
				VariavelProvedor.adicionar(vAuthToken);
			} else {
				vAuthToken.setValor(valor);
			}
		}
	}

	public static void setSetCookie(Map<String, List<String>> map) {
		if (map == null) {
			return;
		}
		List<String> lista = map.get("Set-Cookie");
		if (lista != null && !lista.isEmpty()) {
			String valor = lista.get(0);
			if (!Util.estaVazio(valor)) {
				Variavel vSetCookie = VariavelProvedor.getVariavel(RequisicaoConstantes.VAR_SET_COOKIE);
				if (vSetCookie == null) {
					vSetCookie = new Variavel(RequisicaoConstantes.VAR_SET_COOKIE, valor);
					VariavelProvedor.adicionar(vSetCookie);
				} else {
					vSetCookie.setValor(valor);
				}
			}
		}
	}
}