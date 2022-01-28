package br.com.persist.plugins.requisicao.conteudo;

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
}