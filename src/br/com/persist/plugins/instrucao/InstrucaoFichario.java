package br.com.persist.plugins.instrucao;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Util;

public class InstrucaoFichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public void adicionarPagina(InstrucaoPagina pagina) {
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

	public InstrucaoPagina getPaginaAtiva() {
		int indice = getSelectedIndex();
		if (indice != -1) {
			return (InstrucaoPagina) getComponentAt(indice);
		}
		return null;
	}

	private InstrucaoPagina getPagina(String idPagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof InstrucaoPagina) {
				InstrucaoPagina p = (InstrucaoPagina) cmp;
				if (p.getNome().equals(idPagina)) {
					return p;
				}
			}
		}
		return null;
	}

	private int getIndicePagina(InstrucaoPagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof InstrucaoPagina) {
				InstrucaoPagina p = (InstrucaoPagina) cmp;
				if (p == pagina) {
					return i;
				}
			}
		}
		return -1;
	}

	void setFontTextArea(Font font) {
		for (int i = 0; i < getTabCount(); i++) {
			Component cmp = getComponentAt(i);
			if (cmp instanceof InstrucaoPagina) {
				InstrucaoPagina p = (InstrucaoPagina) cmp;
				p.setFontTextArea(font);
			}
		}
	}

	public void setConteudo(String conteudo, String idPagina) {
		InstrucaoPagina pagina = getPagina(idPagina);
		if (pagina != null) {
			if (!Util.estaVazio(conteudo)) {
				pagina.textArea.setText(conteudo);
			}
			setSelectedIndex(getIndicePagina(pagina));
		}
	}
}