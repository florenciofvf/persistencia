package br.com.persist.plugins.requisicao.visualizador;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.requisicao.RequisicaoConstantes;

public class RequisicaoPoolVisualizador {
	public static final RequisicaoVisualizador VISUALIZADOR_TEXTO = new RequisicaoVisualizadorTexto();
	public static final RequisicaoVisualizador VISUALIZADOR_JSON = new RequisicaoVisualizadorJSON();
	private final RequisicaoVisualizador[] visualizadores;
	private RequisicaoVisualizador selecionado;
	private final File file;

	public RequisicaoPoolVisualizador() {
		visualizadores = new RequisicaoVisualizador[] { null, new RequisicaoVisualizadorImagem(), VISUALIZADOR_TEXTO,
				new RequisicaoVisualizadorHTML(), VISUALIZADOR_JSON, new RequisicaoVisualizadorPDF() };
		file = new File(RequisicaoConstantes.REQUISICOES + Constantes.SEPARADOR + RequisicaoConstantes.MIMES);
	}

	public RequisicaoVisualizador[] getVisualizadores() {
		return visualizadores;
	}

	public void inicializar(Component parent) {
		if (!file.exists()) {
			return;
		}
		limpar();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String linha = br.readLine();
			while (linha != null) {
				processar(linha);
				linha = br.readLine();
			}
		} catch (IOException e) {
			Util.mensagem(parent, e.getMessage());
		}
	}

	private void limpar() {
		selecionado = null;
		for (int i = 1; i < visualizadores.length; i++) {
			visualizadores[i].limpar();
		}
	}

	private void processar(String string) {
		if (Util.isEmpty(string)) {
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
			RequisicaoVisualizador obj = visualizadores[i];
			if (obj.getTitulo().equals(string)) {
				selecionado = obj;
			}
		}
	}

	private void salvar(Component parent) {
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
			for (int i = 1; i < visualizadores.length; i++) {
				visualizadores[i].salvar(pw);
				pw.println();
			}
		} catch (Exception e) {
			Util.mensagem(parent, e.getMessage());
		}
	}

	public void associar(Component parent, String mime, RequisicaoVisualizador visualizador) {
		excluirMime(mime);
		visualizador.adicionarMime(mime);
		salvar(parent);
	}

	private void excluirMime(String mime) {
		for (int i = 1; i < visualizadores.length; i++) {
			visualizadores[i].excluirMime(mime);
		}
	}

	public RequisicaoVisualizador getVisualizador(String mime) {
		for (int i = 1; i < visualizadores.length; i++) {
			RequisicaoVisualizador obj = visualizadores[i];
			if (obj.contemMime(mime)) {
				return obj;
			}
		}
		return null;
	}
}