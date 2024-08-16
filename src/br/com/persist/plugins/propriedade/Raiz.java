package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;

public class Raiz extends Container {
	private static final String SYSTEM_PROPERTIES = "system-properties";
	public static final String RAIZ_CONFIGURACAO = "raizConfiguracao";
	private List<Config> cacheConfigs;

	@Override
	public void adicionar(Container c) throws PropriedadeException {
		if (c instanceof Config || c instanceof Modulo) {
			super.adicionar(c);
		} else {
			throw new PropriedadeException("erro.componente_invalido");
		}
	}

	void gerarProperty(StyledDocument doc) throws BadLocationException {
		for (Modulo modulo : getModulos()) {
			modulo.gerarProperty(doc);
		}
	}

	@Override
	public void print(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagComposta("", SYSTEM_PROPERTIES, doc);
		PropriedadeUtil.fimTagComposta(doc);
		PropriedadeUtil.iniTagComposta(PropriedadeConstantes.TAB, RAIZ_CONFIGURACAO, doc);
		PropriedadeUtil.fimTagComposta(doc);

		for (Config config : getCacheConfigs()) {
			config.print(doc);
			doc.insertString(doc.getLength(), Constantes.QL, null);
		}

		doc.insertString(doc.getLength(), Constantes.QL2, null);
		doc.insertString(doc.getLength(), Constantes.QL2, null);
		doc.insertString(doc.getLength(), Constantes.QL2, null);
		doc.insertString(doc.getLength(), Constantes.QL2, null);

		for (Modulo modulo : getModulos()) {
			doc.insertString(doc.getLength(), Constantes.QL, null);
			modulo.print(doc);
		}

		PropriedadeUtil.fimTagComposta(PropriedadeConstantes.TAB, RAIZ_CONFIGURACAO, doc);
		PropriedadeUtil.fimTagComposta("", SYSTEM_PROPERTIES, doc);
	}

	private List<Modulo> getModulos() {
		List<Modulo> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Modulo) {
				resp.add((Modulo) c);
			}
		}
		return resp;
	}

	List<Config> getCacheConfigs() {
		if (cacheConfigs != null) {
			return cacheConfigs;
		}
		cacheConfigs = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Config) {
				cacheConfigs.add((Config) c);
			}
		}
		return cacheConfigs;
	}
}