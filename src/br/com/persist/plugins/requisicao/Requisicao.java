package br.com.persist.plugins.requisicao;

import java.util.Objects;

import br.com.persist.data.Array;
import br.com.persist.data.Objeto;
import br.com.persist.data.Texto;
import br.com.persist.data.Tipo;
import br.com.persist.data.DataUtil;

public class Requisicao {
	private final Tipo tipo;
	private String desc;
	private String url;

	public Requisicao(Tipo tipo) {
		this.tipo = Objects.requireNonNull(tipo);
		init();
	}

	private void init() {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoUrl = objeto.getValor("url");
			url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
			Tipo tipoDesc = objeto.getValor("desc");
			desc = tipoDesc instanceof Texto ? tipoDesc.toString() : "Objeto sem atributo desc";
		} else if (tipo instanceof Array) {
			Array array = (Array) tipo;
			desc = "Objeto Array [" + array.getElementos().size() + "]";
		} else if (tipo != null) {
			desc = tipo.getClass().getName();
		}
	}

	public Requisicao clonar() {
		Requisicao resp = new Requisicao(tipo);
		resp.desc = desc;
		resp.url = url;
		return resp;
	}

	public String getString() {
		if (tipo instanceof Objeto) {
			Objeto objeto = (Objeto) tipo;
			Tipo tipoUrl = objeto.getValor("url");
			if (tipoUrl instanceof Texto) {
				((Texto) tipoUrl).setAlternativo(url);
			}
			return DataUtil.toString(objeto);
		} else if (tipo != null) {
			return DataUtil.toString(tipo);
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