package br.com.persist.plugins.propriedade;

import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.xml.sax.Attributes;

public class Map extends Container {
	private static final String ATT_ID_CONFIG = "idConfig";
	private static final String ATT_CHAVE = "chave";
	public static final String TAG_MAP = "map";
	private final String idConfig;
	private final String chave;

	public Map(String chave, String idConfig) {
		this.chave = Objects.requireNonNull(chave);
		this.idConfig = Objects.requireNonNull(idConfig);
	}

	public static Map criar(Attributes atts) {
		return new Map(value(atts, ATT_CHAVE), value(atts, ATT_ID_CONFIG));
	}

	public String getChave() {
		return chave;
	}

	public String getIdConfig() {
		return idConfig;
	}

	@Override
	public void adicionar(Container c) throws PropriedadeException {
		throw new PropriedadeException("erro.container_vazio");
	}

	@Override
	public void print(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagSimples(PropriedadeConstantes.TAB3, TAG_MAP, doc);
		PropriedadeUtil.atributo(ATT_CHAVE, chave, doc);
		PropriedadeUtil.atributo(ATT_ID_CONFIG, idConfig, doc);
		PropriedadeUtil.fimTagSimples(doc);
	}

	public String substituir(String string) {
		Modulo modulo = (Modulo) pai;
		Config config = modulo.getConfig(idConfig);
		if (config == null) {
			return string;
		}
		return config.substituir(chave, string);
	}

	@Override
	public String toString() {
		return simpleName() + " [" + ATT_CHAVE + "=" + chave + ", " + ATT_ID_CONFIG + "=" + idConfig + "]";
	}
}