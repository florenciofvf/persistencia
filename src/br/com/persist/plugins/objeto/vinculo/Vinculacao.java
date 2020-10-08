package br.com.persist.plugins.objeto.vinculo;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.plugins.objeto.Objeto;

public class Vinculacao {
	private final List<Pesquisa> pesquisas;

	public Vinculacao() {
		pesquisas = new ArrayList<>();
	}

	public void abrir(String arquivo, Component componente) {
		pesquisas.clear();
		File file = null;
		if (!Util.estaVazio(arquivo)) {
			file = new File(arquivo);
		}
		if (file == null || !file.isFile()) {
			return;
		}
		try {
			VinculoHandler handler = new VinculoHandler();
			XML.processar(file, handler);
			pesquisas.addAll(handler.getPesquisas());
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, componente);
		}
	}

	public void processar(Objeto objeto) {
		for (Pesquisa p : pesquisas) {
			p.processar(objeto);
		}
	}
}