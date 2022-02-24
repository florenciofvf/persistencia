package br.com.persist.plugins.requisicao;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.requisicao.conteudo.ConteudoHTML;
import br.com.persist.plugins.requisicao.conteudo.ConteudoImagem;
import br.com.persist.plugins.requisicao.conteudo.ConteudoJSON;
import br.com.persist.plugins.requisicao.conteudo.ConteudoTexto;
import br.com.persist.plugins.requisicao.conteudo.RequisicaoConteudo;

public class RequisicaoVisualizador {
	private final RequisicaoConteudo[] visualizadores;
	private RequisicaoConteudo selecionado;
	private final File file;

	public RequisicaoVisualizador() {
		visualizadores = new RequisicaoConteudo[] { null, new ConteudoImagem(), new ConteudoTexto(), new ConteudoHTML(),
				new ConteudoJSON() };
		file = new File(RequisicaoConstantes.REQUISICOES + Constantes.SEPARADOR + "mimes");
	}

	public RequisicaoConteudo[] getVisualizadores() {
		return visualizadores;
	}

	public void inicializar(Component parent) {
		if (!file.exists()) {
			return;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String linha = br.readLine();
			while (linha != null) {
				processar(linha);
				linha = br.readLine();
			}
		} catch (IOException e) {
			Util.mensagem(parent, e.getMessage());
		}
	}

	private void processar(String string) {
		if (Util.estaVazio(string)) {
			return;
		}
		if (string.charAt(0) == '\t' && selecionado != null) {
			selecionado.adicionarMime(string);
		} else {
			selecionar(string);
		}
	}

	private void selecionar(String string) {
		selecionado = null;
		for (int i = 1; i < visualizadores.length; i++) {
			RequisicaoConteudo obj = visualizadores[i];
			if (obj.getTitulo().equals(string)) {
				selecionado = obj;
			}
		}
	}

	private void salvar(Component parent) {
		try (PrintWriter pw = new PrintWriter(file)) {
			for (int i = 1; i < visualizadores.length; i++) {
				visualizadores[i].salvar(pw);
				pw.println();
			}
		} catch (FileNotFoundException e) {
			Util.mensagem(parent, e.getMessage());
		}
	}

	public void associar(Component parent, String mime, RequisicaoConteudo requisicaoConteudo) {
		excluirMime(mime);
		requisicaoConteudo.adicionarMime(mime);
		salvar(parent);
	}

	private void excluirMime(String mime) {
		for (int i = 1; i < visualizadores.length; i++) {
			visualizadores[i].excluirMime(mime);
		}
	}

	public RequisicaoConteudo getVisualizador(String mime) {
		for (int i = 1; i < visualizadores.length; i++) {
			RequisicaoConteudo obj = visualizadores[i];
			if (obj.contemMime(mime)) {
				return obj;
			}
		}
		return null;
	}
}