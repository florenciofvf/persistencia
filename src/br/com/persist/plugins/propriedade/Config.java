package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class Config extends Container {
	private List<Campo> cacheCampos;
	private final String id;

	public Config(String id) {
		this.id = Objects.requireNonNull(id);
	}

	public String getId() {
		return id;
	}

	@Override
	public void adicionar(Container c) {
		if (c instanceof Campo) {
			super.adicionar(c);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void color(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagComposta(PropriedadeConstantes.TAB2, "config", doc);
		PropriedadeUtil.atributo("id", id, doc);
		PropriedadeUtil.fimTagComposta(doc);
		for (Campo campo : getCacheCampos()) {
			campo.color(doc);
		}
		PropriedadeUtil.fimTagComposta(PropriedadeConstantes.TAB2, "config", doc);
	}

	List<Campo> getCacheCampos() {
		if (cacheCampos != null) {
			return cacheCampos;
		}
		cacheCampos = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Campo) {
				cacheCampos.add((Campo) c);
			}
		}
		return cacheCampos;
	}

	public String substituir(String chave, String string) {
		for (Campo c : getCacheCampos()) {
			string = Util.replaceAll(string, Constantes.SEP + chave + "." + c.getNome() + Constantes.SEP, c.getValor());
		}
		return string;
	}

	@Override
	public String toString() {
		return "Config [id=" + id + "]";
	}
}