package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;

public class Modulo extends Container {
	private List<Map> cacheMaps;
	private final String nome;
	private boolean invalido;

	public Modulo(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public String getNome() {
		return nome;
	}

	public boolean isInvalido() {
		return invalido;
	}

	public void setInvalido(boolean invalido) {
		this.invalido = invalido;
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
		if (invalido) {
			return;
		}
		PropriedadeUtil.modulo(Constantes.TAB2, getNome(), doc);
		for (Propriedade prop : getPropriedades()) {
			prop.processar(doc);
		}
	}

	@Override
	public void color(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagComposta(Constantes.TAB, "modulo", doc);
		PropriedadeUtil.atributo("nome", nome, doc);
		if (invalido) {
			PropriedadeUtil.atributo("invalido", "true", doc);
		}
		PropriedadeUtil.fimTagComposta(doc);
		for (Map map : getCacheMaps()) {
			map.color(doc);
		}
		if (!getCacheMaps().isEmpty()) {
			doc.insertString(doc.getLength(), Constantes.QL, null);
		}
		for (Propriedade prop : getPropriedades()) {
			prop.color(doc);
		}
		PropriedadeUtil.fimTagComposta(Constantes.TAB, "modulo", doc);
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

	Config getConfig(String id) {
		Raiz raiz = (Raiz) pai;
		for (Config obj : raiz.getCacheConfigs()) {
			if (id.equals(obj.getId())) {
				return obj;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Modulo [nome=" + nome + "]";
	}
}