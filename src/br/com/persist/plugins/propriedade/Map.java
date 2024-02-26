package br.com.persist.plugins.propriedade;

import java.util.Objects;

public class Map extends Container {
	private final String chave;
	private final String idObjeto;

	public Map(String chave, String idObjeto) {
		this.chave = Objects.requireNonNull(chave);
		this.idObjeto = Objects.requireNonNull(idObjeto);
	}

	public String getChave() {
		return chave;
	}

	public String getIdObjeto() {
		return idObjeto;
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}

	public String substituir(String string) {
		Bloco bloco = (Bloco) pai;
		Objeto objeto = bloco.getObjeto(idObjeto);
		return objeto.substituir(chave, string);
	}

	@Override
	public String toString() {
		return "Map [chave=" + chave + ", idObjeto=" + idObjeto + "]";
	}
}