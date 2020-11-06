package br.com.persist.plugins.objeto.vinculo;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.plugins.objeto.Instrucao;
import br.com.persist.plugins.objeto.Objeto;

public class Vinculacao {
	private final Map<String, List<Instrucao>> instrucoes;
	private final List<Pesquisa> pesquisas;

	public Vinculacao() {
		pesquisas = new ArrayList<>();
		instrucoes = new HashMap<>();
	}

	public void abrir(String arquivo, Component componente) {
		instrucoes.clear();
		pesquisas.clear();
		File file = null;
		if (!Util.estaVazio(arquivo)) {
			file = new File(arquivo);
		}
		if (file != null && file.isFile()) {
			try {
				VinculoHandler handler = new VinculoHandler();
				XML.processar(file, handler);
				pesquisas.addAll(handler.getPesquisas());
				instrucoes.putAll(handler.getInstrucoes());
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, componente);
			}
		}
	}

	public void processar(Objeto objeto) {
		List<Instrucao> lista = instrucoes.get(objeto.getTabela2());
		objeto.addInstrucoes(lista);
		for (Pesquisa p : pesquisas) {
			p.processar(objeto);
		}
	}
}