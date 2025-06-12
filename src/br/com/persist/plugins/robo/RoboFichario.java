package br.com.persist.plugins.robo;

import java.awt.Component;
import java.util.Set;

import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Util;

public class RoboFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public RoboFichario() {
		setTabPlacement(RoboPreferencia.getRoboPosicaoAbaFichario());
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

	public void contemConteudo(Set<String> set, String string) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof RoboPagina) {
				RoboPagina p = (RoboPagina) cmp;
				p.contemConteudo(set, string);
			}
		}
		if (set.isEmpty()) {
			Util.beep();
		}
	}

	public void executarTodos() throws InterruptedException {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof RoboPagina) {
				RoboPagina p = (RoboPagina) cmp;
				p.executar();
				Thread.sleep(2000);
			}
		}
	}
}