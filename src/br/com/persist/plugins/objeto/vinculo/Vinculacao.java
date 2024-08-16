package br.com.persist.plugins.objeto.vinculo;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoException;

public class Vinculacao {
	private final Map<String, ParaTabela> mapaParaTabela;
	private static final String VINCULO = "vinculo";
	private final List<Pesquisa> pesquisas;

	public Vinculacao() {
		mapaParaTabela = new LinkedHashMap<>();
		pesquisas = new ArrayList<>();
	}

	public boolean adicionarPesquisa(Pesquisa pesquisa) throws ObjetoException {
		if (pesquisa != null && !PesquisaUtil.contem(pesquisa, pesquisas)) {
			pesquisas.add(pesquisa);
			return true;
		}
		return false;
	}

	public void putParaTabela(ParaTabela paraTabela) {
		mapaParaTabela.put(paraTabela.getTabela(), paraTabela);
	}

	public void abrir(ArquivoVinculo av, Component componente) throws XMLException {
		mapaParaTabela.clear();
		pesquisas.clear();
		File file = null;
		if (av.valido()) {
			file = av.getFile();
		}
		if (file != null && file.isFile()) {
			try {
				VinculoHandler handler = new VinculoHandler();
				XML.processar(file, handler);
				pesquisas.addAll(handler.getPesquisas());
				mapaParaTabela.putAll(handler.getMapaParaTabela());
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, componente);
				throw new XMLException(ex);
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

	public boolean excluir(Pesquisa pesquisa) {
		return pesquisas.remove(pesquisa);
	}

	public void processar(Objeto objeto) throws ObjetoException, AssistenciaException {
		ParaTabela paraTabela = mapaParaTabela.get(objeto.getTabela());
		if (paraTabela != null) {
			objeto.addInstrucoes(paraTabela.getInstrucoes());
			objeto.addFiltros(paraTabela.getFiltros());
			paraTabela.config(objeto);
		}
		for (Pesquisa p : pesquisas) {
			p.processar(objeto);
		}
	}

	public void salvar(ArquivoVinculo av, Component componente) {
		File file = null;
		if (av.valido()) {
			file = av.getFile();
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

	public static void criarArquivoVinculado(ArquivoVinculo av) throws XMLException, ObjetoException {
		av.checarDiretorio();
		XMLUtil util = new XMLUtil(av.getFile());
		util.prologo();
		util.abrirTag2(VINCULO);
		util.print("<!--").ql();
		new ParaTabela(".").modelo(util);
		new Pesquisa(".", new Referencia(null, ".", null), null).modelo(util);
		util.print("-->").ql();
		util.finalizarTag(VINCULO);
		util.close();
	}
}