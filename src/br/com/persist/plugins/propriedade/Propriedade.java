package br.com.persist.plugins.propriedade;

import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;

public class Propriedade extends Container {
	private final String name;
	private final String value;

	public Propriedade(String name, String value) {
		this.name = Objects.requireNonNull(name);
		this.value = Objects.requireNonNull(value);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}

	@Override
	public void processar(StyledDocument doc) throws BadLocationException {
		String string = value;
		Modulo modulo = (Modulo) pai;
		List<Map> maps = modulo.getCacheMaps();
		for (Map map : maps) {
			string = map.substituir(string);
		}
		PropriedadeUtil.iniTagSimples(Constantes.TAB2, "property", doc);
		PropriedadeUtil.atributo("name", name, doc);
		PropriedadeUtil.atributo("value", string, doc);
		PropriedadeUtil.fimTagSimples(doc);
	}

	@Override
	public void color(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagSimples(Constantes.TAB2, "property", doc);
		PropriedadeUtil.atributo("name", name, doc);
		PropriedadeUtil.atributo("value", value, doc);
		PropriedadeUtil.fimTagSimples(doc);
	}

	@Override
	public String toString() {
		return "Propriedade [name=" + name + ", value=" + value + "]";
	}
}