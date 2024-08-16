package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;

public class Modulo extends Container {
	private static final String ATT_INVALIDO = "invalido";
	public static final String TAG_MODULO = "modulo";
	private static final String ATT_NOME = "nome";
	private static final String TRUE = "true";
	private List<Map> cacheMaps;
	private final String nome;
	private boolean invalido;

	public Modulo(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public static Modulo criar(Attributes atts) {
		Modulo modulo = new Modulo(value(atts, ATT_NOME));
		String string = value(atts, ATT_INVALIDO);
		modulo.setInvalido(TRUE.equalsIgnoreCase(string));
		return modulo;
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
	public void adicionar(Container c) throws PropriedadeException {
		if (c instanceof Map || c instanceof Property) {
			super.adicionar(c);
		} else {
			throw new PropriedadeException("erro.componente_invalido");
		}
	}

	void gerarProperty(StyledDocument doc) throws BadLocationException {
		if (invalido) {
			return;
		}
		PropriedadeUtil.modulo(PropriedadeConstantes.TAB2, getNome(), doc);
		for (Property prop : getProperties()) {
			prop.gerarProperty(doc);
		}
	}

	@Override
	public void print(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagComposta(PropriedadeConstantes.TAB2, TAG_MODULO, doc);
		PropriedadeUtil.atributo(ATT_NOME, nome, doc);
		if (invalido) {
			PropriedadeUtil.atributo(ATT_INVALIDO, TRUE, doc);
		}
		PropriedadeUtil.fimTagComposta(doc);

		for (Map map : getCacheMaps()) {
			map.print(doc);
		}

		if (!getCacheMaps().isEmpty()) {
			doc.insertString(doc.getLength(), Constantes.QL, null);
		}

		for (Property prop : getProperties()) {
			prop.print(doc);
		}

		PropriedadeUtil.fimTagComposta(PropriedadeConstantes.TAB2, TAG_MODULO, doc);
	}

	private List<Property> getProperties() {
		List<Property> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Property) {
				resp.add((Property) c);
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
		return simpleName() + " [" + ATT_NOME + "=" + nome + "]";
	}
}