package br.com.persist.plugins.propriedade;

import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

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
	public void processar(Container pai, StyledDocument doc) throws BadLocationException {
		String string = value;
		Bloco bloco = (Bloco) pai;
		List<Map> maps = bloco.getCacheMaps();
		for (Map map : maps) {
			string = map.substituir(string);
		}
		PropriedadeUtil.iniPropriedade("property", doc);
		PropriedadeUtil.atributo("name", doc);
		PropriedadeUtil.valorAtr(name, doc);
		PropriedadeUtil.atributo("value", doc);
		PropriedadeUtil.valorAtr(string, doc);
		PropriedadeUtil.fimPropriedade(doc);
	}

	@Override
	public String toString() {
		return "Propriedade [name=" + name + ", value=" + value + "]";
	}
}