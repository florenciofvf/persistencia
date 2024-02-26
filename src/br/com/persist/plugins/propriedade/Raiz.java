package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class Raiz extends Container {
	private List<Objeto> cacheObjetos;

	@Override
	public void adicionar(Container c) {
		if (c instanceof Objeto || c instanceof Bloco) {
			super.adicionar(c);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void processar(Container pai, StyledDocument doc) throws BadLocationException {
		for (Bloco bloco : getBlocos()) {
			bloco.processar(this, doc);
		}
	}

	private List<Bloco> getBlocos() {
		List<Bloco> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Bloco) {
				resp.add((Bloco) c);
			}
		}
		return resp;
	}

	List<Objeto> getCacheObjetos() {
		if (cacheObjetos != null) {
			return cacheObjetos;
		}
		cacheObjetos = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Objeto) {
				cacheObjetos.add((Objeto) c);
			}
		}
		return cacheObjetos;
	}
}