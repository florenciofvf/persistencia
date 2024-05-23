package br.com.persist.plugins.propriedade;

import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class Map extends Container {
	private final String chave;
	private final String idConfig;

	public Map(String chave, String idConfig) {
		this.chave = Objects.requireNonNull(chave);
		this.idConfig = Objects.requireNonNull(idConfig);
	}

	public String getChave() {
		return chave;
	}

	public String getIdConfig() {
		return idConfig;
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}

	@Override
	public void color(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagSimples(PropriedadeConstantes.TAB3, "map", doc);
		PropriedadeUtil.atributo("chave", chave, doc);
		PropriedadeUtil.atributo("idConfig", idConfig, doc);
		PropriedadeUtil.fimTagSimples(doc);
	}

	public String substituir(String string) {
		Modulo modulo = (Modulo) pai;
		Config config = modulo.getConfig(idConfig);
		return config.substituir(chave, string);
	}

	@Override
	public String toString() {
		return "Map [chave=" + chave + ", idConfig=" + idConfig + "]";
	}
}