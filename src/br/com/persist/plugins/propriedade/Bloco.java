package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;

public class Bloco extends Container {
	private List<Map> cacheMaps;
	private final String nome;

	public Bloco(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public String getNome() {
		return nome;
	}

	@Override
	public void adicionar(Container c) {
		if (c instanceof Map || c instanceof Propriedade) {
			super.adicionar(c);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void processar(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.bloco(Constantes.TAB, getNome(), doc);
		for (Propriedade prop : getPropriedades()) {
			prop.processar(doc);
		}
	}

	@Override
	public void color(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagComposta(Constantes.TAB, "bloco", doc);
		PropriedadeUtil.atributo("nome", nome, doc);
		PropriedadeUtil.fimTagComposta(doc);
		for (Map map : getCacheMaps()) {
			map.color(doc);
		}
		doc.insertString(doc.getLength(), Constantes.QL, null);
		for (Propriedade prop : getPropriedades()) {
			prop.color(doc);
		}
		PropriedadeUtil.fimTagComposta(Constantes.TAB, "bloco", doc);
	}

	private List<Propriedade> getPropriedades() {
		List<Propriedade> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Propriedade) {
				resp.add((Propriedade) c);
			}
		}
		return resp;
	}

	List<Map> getCacheMaps() {
		if (cacheMaps != null) {
			return cacheMaps;
		}
		cacheMaps = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Map) {
				cacheMaps.add((Map) c);
			}
		}
		return cacheMaps;
	}

	Objeto getObjeto(String id) {
		Raiz raiz = (Raiz) pai;
		for (Objeto obj : raiz.getCacheObjetos()) {
			if (id.equals(obj.getId())) {
				return obj;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Bloco [nome=" + nome + "]";
	}
}