package br.com.persist.plugins.requisicao;

import java.util.Objects;

import br.com.persist.parser.Objeto;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;
import br.com.persist.parser.TipoUtil;

public class Requisicao {
	private final Tipo tipo;
	private String desc;
	private String url;

	public Requisicao(Tipo tipo) {
		Objects.requireNonNull(tipo);
		this.tipo = tipo;
		init();
	}

	private void init() {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoUrl = objeto.getValor("url");
			url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
			Tipo tipoDesc = objeto.getValor("desc");
			desc = tipoDesc instanceof Texto ? tipoDesc.toString() : null;
		}
	}

	public String getString() {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoUrl = objeto.getValor("url");
			if (tipoUrl instanceof Texto) {
				((Texto) tipoUrl).setAlternativo(url);
			}
			return TipoUtil.toString(objeto);
		}
		return null;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Tipo getTipo() {
		return tipo;
	}
}