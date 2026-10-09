package br.com.persist.plugins.labirinto;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.persist.abstrato.Aba;
import br.com.persist.abstrato.AbstratoFichario;
import br.com.persist.assistencia.Util;

public class LabirintoFichario extends AbstratoFichario {
	private static final long serialVersionUID = 1L;
	private final LabirintoContainer container;

	public LabirintoFichario(LabirintoContainer container) {
		super(false);
		setTabPlacement(LabirintoPreferencia.getLabirintoPosicaoAbaFichario());
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		this.container = container;
	}

	public void adicionarPagina(LabirintoPagina pagina) {
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

	public LabirintoPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (LabirintoPagina) getComponentAt(indice);
		}
		return null;
	}

	public void salvar() {
		container.salvar();
	}

	private LabirintoPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof LabirintoPagina) {
				LabirintoPagina p = (LabirintoPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(LabirintoPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof LabirintoPagina) {
				LabirintoPagina p = (LabirintoPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		LabirintoPagina pagina = getPagina(idPagina);
		if (pagina != null) {
			if (!Util.isEmpty(conteudo)) {
				pagina.textEditor.setText(conteudo);
			}
			setSelectedIndex(getIndicePagina(pagina));
		}
	}

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof LabirintoPagina) {
				LabirintoPagina p = (LabirintoPagina) cmp;
				p.contemConteudo(set, string, porParte);
			}
		}
		if (set.isEmpty()) {
			Util.beep();
		}
	}

	@Override
	public List<Aba> getAbas() {
		List<Aba> resposta = new ArrayList<>();
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof Aba) {
				((Aba) cmp).setIndice(i);
				resposta.add((Aba) cmp);
			}
		}
		return resposta;
	}
}