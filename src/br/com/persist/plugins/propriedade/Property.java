package br.com.persist.plugins.propriedade;

import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.xml.sax.Attributes;

public class Property extends Container {
	public static final String TAG_PROPERTY = "property";
	private static final String ATT_VALUE = "value";
	private static final String ATT_NAME = "name";
	private final String value;
	private final String name;

	public Property(String name, String value) {
		this.name = Objects.requireNonNull(name);
		this.value = Objects.requireNonNull(value);
	}

	public static Property criar(Attributes atts) {
		return new Property(value(atts, ATT_NAME), value(atts, ATT_VALUE));
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void adicionar(Container c) throws PropriedadeException {
		throw new PropriedadeException("erro.container_vazio");
	}

	void gerarProperty(StyledDocument doc) throws BadLocationException {
		String string = value;
		Modulo modulo = (Modulo) pai;
		List<Map> maps = modulo.getCacheMaps();
		for (Map map : maps) {
			string = map.substituir(string);
		}
		PropriedadeUtil.iniTagSimples(PropriedadeConstantes.TAB2, TAG_PROPERTY, doc);
		PropriedadeUtil.atributo(ATT_NAME, name, doc);
		PropriedadeUtil.atributo(ATT_VALUE, string, doc);
		PropriedadeUtil.fimTagSimples(doc);
	}

	@Override
	public void print(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagSimples(PropriedadeConstantes.TAB3, TAG_PROPERTY, doc);
		PropriedadeUtil.atributo(ATT_NAME, name, doc);
		PropriedadeUtil.atributo(ATT_VALUE, value, doc);
		PropriedadeUtil.fimTagSimples(doc);
	}

	@Override
	public String toString() {
		return simpleName() + " [" + ATT_NAME + "=" + name + ", " + ATT_VALUE + "=" + value + "]";
	}
}