package br.com.persist.plugins.propriedade;

import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;

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

	@Override
	public void color(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagSimples(Constantes.TAB2, "map", doc);
		PropriedadeUtil.atributo("chave", chave, doc);
		PropriedadeUtil.atributo("idObjeto", idObjeto, doc);
		PropriedadeUtil.fimTagSimples(doc);
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