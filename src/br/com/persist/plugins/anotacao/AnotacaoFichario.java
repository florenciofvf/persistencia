package br.com.persist.plugins.anotacao;

import java.awt.Component;

import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Util;

public class AnotacaoFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public void adicionarPagina(AnotacaoPagina pagina) {
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

	public AnotacaoPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (AnotacaoPagina) getComponentAt(indice);
		}
		return null;
	}

	private AnotacaoPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof AnotacaoPagina) {
				AnotacaoPagina p = (AnotacaoPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(AnotacaoPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof AnotacaoPagina) {
				AnotacaoPagina p = (AnotacaoPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		AnotacaoPagina pagina = getPagina(idPagina);
		if (pagina != null) {
			if (!Util.estaVazio(conteudo)) {
				pagina.areaParametros.setText(conteudo);
			}
			setSelectedIndex(getIndicePagina(pagina));
		}
	}
}