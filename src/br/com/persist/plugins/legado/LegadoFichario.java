package br.com.persist.plugins.legado;

import java.awt.Component;
import java.util.Set;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.AbstratoFichario;

public class LegadoFichario extends AbstratoFichario {
	private static final long serialVersionUID = 1L;

	public LegadoFichario() {
		setTabPlacement(LegadoPreferencia.getLegadoPosicaoAbaFichario());
	}

	public void adicionarPagina(LegadoPagina pagina) {
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

	public LegadoPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (LegadoPagina) getComponentAt(indice);
		}
		return null;
	}

	private LegadoPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof LegadoPagina) {
				LegadoPagina p = (LegadoPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(LegadoPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof LegadoPagina) {
				LegadoPagina p = (LegadoPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		LegadoPagina pagina = getPagina(idPagina);
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
			if (cmp instanceof LegadoPagina) {
				LegadoPagina p = (LegadoPagina) cmp;
				p.contemConteudo(set, string);
			}
		}
		if (set.isEmpty()) {
			Util.beep();
		}
	}
}