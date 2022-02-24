package br.com.persist.plugins.requisicao.conteudo;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.requisicao.RequisicaoRota;

public abstract class AbstratoRequisicaoConteudo implements RequisicaoConteudo {
	private RequisicaoConteudoListener requisicaoConteudoListener;
	private RequisicaoRota requisicaoRota;
	private final Set<String> mimes;

	public AbstratoRequisicaoConteudo() {
		mimes = new HashSet<>();
	}

	@Override
	public Set<String> getMimes() {
		return mimes;
	}

	@Override
	public void adicionarMime(String mime) {
		if (!Util.estaVazio(mime)) {
			mimes.add(mime.trim());
		}
	}

	@Override
	public void excluirMime(String mime) {
		if (!Util.estaVazio(mime)) {
			mimes.remove(mime.trim());
		}
	}

	@Override
	public boolean contemMime(String mime) {
		if (!Util.estaVazio(mime)) {
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

	public RequisicaoConteudoListener getRequisicaoConteudoListener() {
		return requisicaoConteudoListener;
	}

	public void setRequisicaoConteudoListener(RequisicaoConteudoListener requisicaoConteudoListener) {
		this.requisicaoConteudoListener = requisicaoConteudoListener;
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