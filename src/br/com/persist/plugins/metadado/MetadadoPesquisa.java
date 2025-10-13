package br.com.persist.plugins.metadado;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Busca;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;

public class MetadadoPesquisa implements Busca {
	private List<Metadado> lista = new ArrayList<>();
	private final MetadadoTree metadadoTree;
	final boolean porParte;
	final String string;
	int indice;

	public MetadadoPesquisa(MetadadoTree metadadoTree, String string, boolean porParte) {
		this.metadadoTree = metadadoTree;
		this.porParte = porParte;
		this.string = string;
		if (metadadoTree != null && !Util.isEmpty(string)) {
			metadadoTree.preencher(lista, string, porParte);
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
			Metadado metadado = lista.get(indice);
			MetadadoTreeUtil.selecionarObjeto(metadadoTree, metadado);
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
		metadadoTree.clearSelection();
	}
}