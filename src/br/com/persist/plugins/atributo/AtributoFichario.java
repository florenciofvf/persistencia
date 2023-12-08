package br.com.persist.plugins.atributo;

import java.awt.Component;

import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Util;

public class AtributoFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public AtributoFichario() {
		setTabPlacement(AtributoPreferencia.getAtributoPosicaoAbaFichario());
	}

	public void adicionarPagina(AtributoPagina pagina) {
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

	public AtributoPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (AtributoPagina) getComponentAt(indice);
		}
		return null;
	}

	private AtributoPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof AtributoPagina) {
				AtributoPagina p = (AtributoPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(AtributoPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof AtributoPagina) {
				AtributoPagina p = (AtributoPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		AtributoPagina pagina = getPagina(idPagina);
		if (pagina != null) {
			if (!Util.isEmpty(conteudo)) {
				pagina.setText(conteudo);
			}
			setSelectedIndex(getIndicePagina(pagina));
		}
	}
}