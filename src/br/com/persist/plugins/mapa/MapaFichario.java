package br.com.persist.plugins.mapa;

import java.awt.Component;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.AbstratoFichario;

public class MapaFichario extends AbstratoFichario {
	private static final long serialVersionUID = 1L;

	public MapaFichario() {
		setTabPlacement(MapaPreferencia.getMapaPosicaoAbaFichario());
	}

	public void adicionarPagina(MapaPagina pagina) {
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

	public MapaPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (MapaPagina) getComponentAt(indice);
		}
		return null;
	}

	private MapaPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof MapaPagina) {
				MapaPagina p = (MapaPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(MapaPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof MapaPagina) {
				MapaPagina p = (MapaPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		MapaPagina pagina = getPagina(idPagina);
		if (pagina != null) {
			if (!Util.isEmpty(conteudo)) {
				pagina.setConteudo(conteudo);
			}
			setSelectedIndex(getIndicePagina(pagina));
		}
	}
}