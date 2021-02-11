package br.com.persist.plugins.check;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.marca.XML;

public class Check {
	private static final Map<String, Arquivo> map = new HashMap<>();
	private static final Logger LOG = Logger.getGlobal();
	private static Arquivo arquivo;

	private Check() {
	}

	public static void selecionar(String string) {
		arquivo = map.get(string);
		if (arquivo == null && new File(string).exists()) {
			arquivo = carregar(string);
			if (arquivo != null) {
				arquivo.inicializar();
				map.put(string, arquivo);
			} else {
				throw new IllegalStateException();
			}
		}
	}

	private static Arquivo carregar(String string) {
		try {
			SentencaColetor coletor = new SentencaColetor();
			XML.processar(new File(string), new SentencaHandler(coletor));
			return new Arquivo(coletor.getSentencas());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
		return null;
	}

	public static List<Object> check(Map<String, Object> map) {
		if (arquivo == null) {
			throw new IllegalStateException("Selecione o arquivo para checagem.");
		}
		return arquivo.check(map);
	}
}