package br.com.persist.plugins.atributo;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.Aba;
import br.com.persist.componente.AbstratoFichario;

public class AtributoFichario extends AbstratoFichario {
	private static final long serialVersionUID = 1L;
	private final AtributoContainer container;

	public AtributoFichario(AtributoContainer container) {
		setTabPlacement(AtributoPreferencia.getAtributoPosicaoAbaFichario());
		this.container = container;
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

	public void salvar() {
		container.salvar();
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

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof AtributoPagina) {
				AtributoPagina p = (AtributoPagina) cmp;
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