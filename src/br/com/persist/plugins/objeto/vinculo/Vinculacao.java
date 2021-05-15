package br.com.persist.plugins.objeto.vinculo;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.plugins.objeto.Objeto;

public class Vinculacao {
	private final Map<String, ParaTabela> mapaParaTabela;
	private final List<Pesquisa> pesquisas;

	public Vinculacao() {
		mapaParaTabela = new HashMap<>();
		pesquisas = new ArrayList<>();
	}

	public void abrir(String arquivo, Component componente) {
		mapaParaTabela.clear();
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
				mapaParaTabela.putAll(handler.getMapaParaTabela());
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, componente);
			}
		}
	}

	public void processar(Objeto objeto) {
		ParaTabela paraTabela = mapaParaTabela.get(objeto.getTabela2());
		if (paraTabela != null) {
			objeto.addInstrucoes(paraTabela.getInstrucoes());
			paraTabela.config(objeto);
		}
		for (Pesquisa p : pesquisas) {
			p.processar(objeto);
		}
	}
}