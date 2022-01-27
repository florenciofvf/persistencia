package br.com.persist.plugins.requisicao;

import java.awt.Component;

import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.requisicao.RequisicaoContainer.Pagina;

public class RequisicaoFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public void adicionarPagina(Pagina pagina) {
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

	public Pagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (Pagina) getComponentAt(indice);
		}
		return null;
	}

	private Pagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof Pagina) {
				Pagina p = (Pagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(Pagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof Pagina) {
				Pagina p = (Pagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		Pagina pagina = getPagina(idPagina);
		if (pagina != null) {
			if (!Util.estaVazio(conteudo)) {
				pagina.areaParametros.setText(conteudo);
			}
			setSelectedIndex(getIndicePagina(pagina));
		}
	}
}