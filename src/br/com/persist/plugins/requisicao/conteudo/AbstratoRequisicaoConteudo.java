package br.com.persist.plugins.requisicao.conteudo;

import br.com.persist.plugins.requisicao.RequisicaoRota;

public abstract class AbstratoRequisicaoConteudo implements RequisicaoConteudo {
	private RequisicaoConteudoListener requisicaoConteudoListener;
	private RequisicaoRota requisicaoRota;

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
}