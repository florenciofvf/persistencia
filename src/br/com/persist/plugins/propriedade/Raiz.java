package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;

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
	public void processar(StyledDocument doc) throws BadLocationException {
		for (Bloco bloco : getBlocos()) {
			bloco.processar(doc);
		}
	}

	@Override
	public void color(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagComposta("", "system-properties", doc);
		PropriedadeUtil.fimTagComposta(doc);
		for (Objeto objeto : getCacheObjetos()) {
			objeto.color(doc);
			doc.insertString(doc.getLength(), Constantes.QL, null);
		}
		doc.insertString(doc.getLength(), Constantes.QL, null);
		for (Bloco bloco : getBlocos()) {
			bloco.color(doc);
		}
		PropriedadeUtil.fimTagComposta("", "system-properties", doc);
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