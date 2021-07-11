package br.com.persist.plugins.objeto.vinculo;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;

public class Vinculacao {
	private final Map<String, ParaTabela> mapaParaTabela;
	private static final String VINCULO = "vinculo";
	private final List<Pesquisa> pesquisas;

	public Vinculacao() {
		mapaParaTabela = new LinkedHashMap<>();
		pesquisas = new ArrayList<>();
	}

	public boolean adicionarPesquisa(Pesquisa pesquisa) {
		if (pesquisa != null && !Pesquisa.contem(pesquisa, pesquisas)) {
			pesquisas.add(pesquisa);
			return true;
		}
		return false;
	}

	public void putParaTabela(ParaTabela paraTabela) {
		mapaParaTabela.put(paraTabela.getTabela(), paraTabela);
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

	public ParaTabela getParaTabela(String tabela) {
		return mapaParaTabela.get(tabela);
	}

	public List<Pesquisa> getPesquisas(Objeto objeto) {
		List<Pesquisa> lista = new ArrayList<>();
		for (Pesquisa p : pesquisas) {
			if (p.igual(objeto)) {
				lista.add(p);
			}
		}
		return lista;
	}

	public Pesquisa getPesquisa(Pesquisa pesquisa) {
		for (Pesquisa p : pesquisas) {
			if (p.igual(pesquisa)) {
				return p;
			}
		}
		return null;
	}

	public void processar(Objeto objeto) {
		ParaTabela paraTabela = mapaParaTabela.get(objeto.getTabela());
		if (paraTabela != null) {
			objeto.addInstrucoes(paraTabela.getInstrucoes());
			paraTabela.config(objeto);
		}
		for (Pesquisa p : pesquisas) {
			p.processar(objeto);
		}
	}

	public void salvar(String arquivo, Component componente) {
		File file = null;
		if (!Util.estaVazio(arquivo)) {
			file = new File(arquivo);
		}
		if (file != null && file.isFile()) {
			try {
				boolean ql = false;
				XMLUtil util = new XMLUtil(file);
				util.prologo();
				util.abrirTag2(VINCULO);
				for (ParaTabela paraTabela : mapaParaTabela.values()) {
					paraTabela.salvar(util, ql);
					ql = true;
				}
				for (Pesquisa pesquisa : pesquisas) {
					pesquisa.salvar(util, ql);
					ql = true;
				}
				util.finalizarTag(VINCULO);
				util.close();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR: " + file.getAbsolutePath(), ex, componente);
			}
		}
	}

	public static void criarArquivoVinculado(File file) throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2(VINCULO);
		util.print("<!--").ql();
		new ParaTabela(".").modelo(util);
		new Pesquisa(".", new Referencia(null, ".", null)).modelo(util);
		util.print("-->").ql();
		util.finalizarTag(VINCULO);
		util.close();
	}
}