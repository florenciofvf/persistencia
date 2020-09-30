package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.parser.Array;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Parser;
import br.com.persist.parser.Tipo;

public class BuscaAutoUtil {
	private BuscaAutoUtil() {
	}

	public static List<GrupoBuscaAuto> listarGrupoBuscaAuto(String string) {
		List<GrupoBuscaAuto> lista = new ArrayList<>();
		if (!Util.estaVazio(string)) {
			try {
				Parser parser = new Parser();
				Tipo tipo = parser.parse(string);
				processar(lista, tipo);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("BuscaAuto", ex, null);
			}
		}
		integrarGrupos(lista);
		return lista;
	}

	private static void integrarGrupos(List<GrupoBuscaAuto> lista) {
		for (int i = 0; i < lista.size(); i++) {
			GrupoBuscaAuto grupo = lista.get(i);
			List<GrupoBuscaAuto> outros = listarOutros(grupo, lista);
			integrar(grupo, outros);
		}
	}

	private static void integrar(GrupoBuscaAuto grupo, List<GrupoBuscaAuto> outros) {
		GrupoLinkAuto grupoLink = grupo.getGrupoLinkAuto();
		for (GrupoBuscaAuto outro : outros) {
			GrupoLinkAuto linkOutro = outro.getGrupoLinkAuto();
			grupoLink.add(linkOutro.getTabelas());
		}
	}

	private static List<GrupoBuscaAuto> listarOutros(GrupoBuscaAuto grupo, List<GrupoBuscaAuto> lista) {
		List<GrupoBuscaAuto> resposta = new ArrayList<>();
		for (GrupoBuscaAuto g : lista) {
			if (g != grupo && g.campoIgual(grupo)) {
				resposta.add(g);
			}
		}
		return resposta;
	}

	private static void processar(List<GrupoBuscaAuto> lista, Tipo tipo) {
		if (tipo instanceof Array) {
			for (Tipo grupo : ((Array) tipo).getLista()) {
				processarGrupo(lista, grupo);
			}
		}
	}

	private static void processarGrupo(List<GrupoBuscaAuto> lista, Tipo tipo) {
		if (tipo instanceof Objeto) {
			Objeto grupo = (Objeto) tipo;
			Map<String, String> map = grupo.getAtributosString();
			GrupoBuscaAuto buscaAuto = new GrupoBuscaAuto(map.get("grupo"), map.get("campo"));
			processarTabelas(buscaAuto, grupo.getValor("tabelas"));
			processarLimparApos(buscaAuto.getGrupoBuscaAutoApos(), grupo.getValor("limparApos"));
			lista.add(buscaAuto);
		}
	}

	private static void processarTabelas(GrupoBuscaAuto buscaAuto, Tipo tipo) {
		GrupoLinkAuto linkAuto = buscaAuto.getGrupoLinkAuto();
		if (tipo instanceof Array) {
			for (Tipo tabela : ((Array) tipo).getLista()) {
				if (tabela instanceof Objeto) {
					Objeto obj = (Objeto) tabela;
					Map<String, String> map = obj.getAtributosString();
					String apelido = map.get("apelido");
					String campo = map.get("campo");
					String nome = map.get("nome");
					TabelaLinkAuto tabelaLink = new TabelaLinkAuto(apelido, nome, campo);
					TabelaBuscaAuto tabelaAuto = new TabelaBuscaAuto(apelido, nome, campo);
					tabelaAuto.setVazioInvisivel("invisivel".equalsIgnoreCase(map.get("vazio")));
					buscaAuto.add(tabelaAuto);
					linkAuto.add(tabelaLink);
				}
			}
		}
	}

	private static void processarLimparApos(GrupoBuscaAutoApos buscaAutoApos, Tipo tipo) {
		if (tipo instanceof Array) {
			for (Tipo tabela : ((Array) tipo).getLista()) {
				if (tabela instanceof Objeto) {
					Objeto obj = (Objeto) tabela;
					Map<String, String> map = obj.getAtributosString();
					TabelaBuscaAutoApos tabelaAutoApos = new TabelaBuscaAutoApos(map.get("apelido"), map.get("nome"));
					buscaAutoApos.add(tabelaAutoApos);
				}
			}
		}
	}
}