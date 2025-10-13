package br.com.persist.plugins.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Busca;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;

public class ArquivoPesquisa implements Busca {
	private List<Arquivo> lista = new ArrayList<>();
	private final ArquivoTree arquivoTree;
	final boolean porParte;
	final String string;
	int indice;

	public ArquivoPesquisa(ArquivoTree arquivoTree, String string, boolean porParte) {
		this.arquivoTree = arquivoTree;
		this.porParte = porParte;
		this.string = string;
		if (arquivoTree != null && !Util.isEmpty(string)) {
			arquivoTree.preencher(lista, string, porParte);
		}
	}

	public boolean igual(String string, boolean porParte) {
		return Util.iguaisEm(this.string, string, this.porParte, porParte);
	}

	public String getString() {
		return string;
	}

	public int getTotal() {
		return lista.size();
	}

	public int getIndice() {
		return indice;
	}

	public void selecionar(Label label) {
		if (label == null) {
			return;
		}
		if (indice < getTotal()) {
			Arquivo arquivo = lista.get(indice);
			ArquivoTreeUtil.selecionarObjeto(arquivoTree, arquivo);
			indice++;
			label.setText(indice + "/" + getTotal());
		} else {
			limparSelecao();
			label.limpar();
			indice = 0;
		}
	}

	@Override
	public void limparSelecao() {
		arquivoTree.clearSelection();
	}
}