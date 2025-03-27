package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class Config extends Container {
	public static final String TAG_CONFIG = "config";
	private static final String ATT_ID = "id";
	private List<Campo> cacheCampos;
	private final String id;

	public Config(String id) {
		this.id = Objects.requireNonNull(id);
	}

	public static Config criar(Attributes atts) {
		return new Config(value(atts, ATT_ID));
	}

	public String getId() {
		return id;
	}

	@Override
	public void adicionar(Container c) throws PropriedadeException {
		if (c instanceof Campo) {
			super.adicionar(c);
		} else {
			throw new PropriedadeException("erro.componente_invalido");
		}
	}

	@Override
	public void print(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagComposta(PropriedadeConstantes.TAB2, TAG_CONFIG, doc);
		PropriedadeUtil.atributo(ATT_ID, id, doc);
		PropriedadeUtil.fimTagComposta(doc);
		for (Campo campo : getCacheCampos()) {
			campo.print(doc);
		}
		PropriedadeUtil.fimTagComposta(PropriedadeConstantes.TAB2, TAG_CONFIG, doc);
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
			if (c.isInvalido()) {
				continue;
			}
			string = Util.replaceAll(string, Constantes.SEP + chave + "." + c.getNome() + Constantes.SEP, c.getValor());
		}
		return string;
	}

	@Override
	public String toString() {
		return simpleName() + " [" + ATT_ID + "=" + id + "]";
	}
}