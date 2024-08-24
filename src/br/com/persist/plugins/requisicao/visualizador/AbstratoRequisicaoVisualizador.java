package br.com.persist.plugins.requisicao.visualizador;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.requisicao.RequisicaoRota;

public abstract class AbstratoRequisicaoVisualizador implements RequisicaoVisualizador {
	private RequisicaoVisualizadorListener requisicaoVisualizadorListener;
	private RequisicaoRota requisicaoRota;
	private final Set<String> mimes;

	protected AbstratoRequisicaoVisualizador() {
		mimes = new HashSet<>();
	}

	@Override
	public Set<String> getMimes() {
		return mimes;
	}

	@Override
	public void adicionarMime(String mime) {
		if (!Util.isEmpty(mime)) {
			mimes.add(mime.trim());
		}
	}

	@Override
	public void excluirMime(String mime) {
		if (!Util.isEmpty(mime)) {
			mimes.remove(mime.trim());
		}
	}

	@Override
	public boolean contemMime(String mime) {
		if (!Util.isEmpty(mime)) {
			return mimes.contains(mime.trim());
		}
		return false;
	}

	@Override
	public void salvar(PrintWriter pw) {
		pw.println(getTitulo());
		for (String string : mimes) {
			pw.println("\t" + string);
		}
	}

	@Override
	public void limpar() {
		mimes.clear();
	}

	public void setRequisicaoVisualizadorListener(RequisicaoVisualizadorListener requisicaoVisualizadorListener) {
		this.requisicaoVisualizadorListener = requisicaoVisualizadorListener;
	}

	public RequisicaoVisualizadorListener getRequisicaoVisualizadorListener() {
		return requisicaoVisualizadorListener;
	}

	public RequisicaoRota getRequisicaoRota() {
		return requisicaoRota;
	}

	public void setRequisicaoRota(RequisicaoRota requisicaoRota) {
		this.requisicaoRota = requisicaoRota;
	}

	@Override
	public String getTitulo() {
		return toString();
	}
}