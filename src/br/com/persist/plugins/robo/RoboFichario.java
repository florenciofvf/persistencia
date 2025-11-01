package br.com.persist.plugins.robo;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.Aba;
import br.com.persist.componente.AbstratoFichario;

public class RoboFichario extends AbstratoFichario {
	private static final long serialVersionUID = 1L;
	private final RoboContainer container;

	public RoboFichario(RoboContainer container) {
		setTabPlacement(RoboPreferencia.getRoboPosicaoAbaFichario());
		this.container = container;
	}

	public void adicionarPagina(RoboPagina pagina) {
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

	public RoboPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (RoboPagina) getComponentAt(indice);
		}
		return null;
	}

	public void salvar() {
		container.salvar();
	}

	private RoboPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof RoboPagina) {
				RoboPagina p = (RoboPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(RoboPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof RoboPagina) {
				RoboPagina p = (RoboPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setConteudo(String conteudo, String idPagina) {
		RoboPagina pagina = getPagina(idPagina);
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
			if (cmp instanceof RoboPagina) {
				RoboPagina p = (RoboPagina) cmp;
				p.contemConteudo(set, string, porParte);
			}
		}
		if (set.isEmpty()) {
			Util.beep();
		}
	}

	public void executarTodos() {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof RoboPagina) {
				RoboPagina p = (RoboPagina) cmp;
				p.executar();
			}
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