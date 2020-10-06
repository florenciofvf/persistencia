package br.com.persist.plugins.objeto.vinculo;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.plugins.objeto.Objeto;

public class Vinculacao {
	private final List<Grupo> grupos;

	public Vinculacao() {
		grupos = new ArrayList<>();
	}

	public void abrir(String arquivo, Component componente) {
		grupos.clear();
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
			grupos.addAll(handler.getGrupos());
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, componente);
		}
		integrarLinks();
	}

	private void integrarLinks() {
		for (int i = 0; i < grupos.size(); i++) {
			Grupo grupo = grupos.get(i);
			List<Grupo> outros = listarOutros(grupo);
			integrar(grupo, outros);
		}
	}

	private List<Grupo> listarOutros(Grupo grupo) {
		List<Grupo> resposta = new ArrayList<>();
		for (Grupo g : grupos) {
			if (g != grupo && g.igual(grupo)) {
				resposta.add(g);
			}
		}
		return resposta;
	}

	private void integrar(Grupo grupo, List<Grupo> outros) {
		for (Grupo outro : outros) {
			grupo.addLink(outro.getClonarReferencias());
		}
	}

	public void processar(Objeto objeto) {
		for (Grupo g : grupos) {
			g.processar(objeto);
		}
	}
}