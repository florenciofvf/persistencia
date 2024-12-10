package br.com.persist.plugins.requisicao;

import java.awt.Component;

import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Util;

public class RequisicaoFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public void adicionarPagina(RequisicaoPagina pagina) {
		addTab(pagina.getNome(), pagina);
		int ultimoIndice = getTabCount() - 1;
		setSelectedIndex(ultimoIndice);
	}

	public void excluirPaginas() {
		while (getTabCount() > 0) {
			removeTabAt(0);
		}
	}

	public int getIndiceAtivo() {
		return getSelectedIndex();
	}

	public RequisicaoPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (RequisicaoPagina) getComponentAt(indice);
		}
		return null;
	}

	private RequisicaoPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof RequisicaoPagina) {
				RequisicaoPagina p = (RequisicaoPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(RequisicaoPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof RequisicaoPagina) {
				RequisicaoPagina p = (RequisicaoPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		RequisicaoPagina pagina = getPagina(idPagina);
		if (pagina != null) {
			if (!Util.isEmpty(conteudo)) {
				pagina.textEditorReq.setText(conteudo);
			}
			setSelectedIndex(getIndicePagina(pagina));
		}
	}
}