package br.com.persist.plugins.entrega;

import java.awt.Component;
import java.util.Set;

import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Util;

public class EntregaFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public EntregaFichario() {
		setTabPlacement(EntregaPreferencia.getEntregaPosicaoAbaFichario());
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
	}

	public void adicionarPagina(EntregaPagina pagina) {
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

	public EntregaPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (EntregaPagina) getComponentAt(indice);
		}
		return null;
	}

	private EntregaPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof EntregaPagina) {
				EntregaPagina p = (EntregaPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(EntregaPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof EntregaPagina) {
				EntregaPagina p = (EntregaPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		EntregaPagina pagina = getPagina(idPagina);
		if (pagina != null) {
			if (!Util.isEmpty(conteudo)) {
				pagina.textEditor.setText(conteudo);
			}
			setSelectedIndex(getIndicePagina(pagina));
		}
	}

	public void contemConteudo(Set<String> set, String string) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof EntregaPagina) {
				EntregaPagina p = (EntregaPagina) cmp;
				p.contemConteudo(set, string);
			}
		}
		if (set.isEmpty()) {
			Util.beep();
		}
	}
}