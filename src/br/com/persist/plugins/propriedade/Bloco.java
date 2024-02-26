package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

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
	public void processar(Container pai, StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.bloco(getNome(), doc);
		for (Propriedade prop : getPropriedades()) {
			prop.processar(this, doc);
		}
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