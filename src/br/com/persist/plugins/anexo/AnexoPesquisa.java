package br.com.persist.plugins.anexo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Busca;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;

public class AnexoPesquisa implements Busca {
	private List<Anexo> lista = new ArrayList<>();
	private final AnexoTree anexoTree;
	final boolean porParte;
	final String string;
	int indice;

	public AnexoPesquisa(AnexoTree anexoTree, String string, boolean porParte) {
		this.anexoTree = anexoTree;
		this.porParte = porParte;
		this.string = string;
		if (anexoTree != null && !Util.isEmpty(string)) {
			anexoTree.preencher(lista, string, porParte);
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
			Anexo anexo = lista.get(indice);
			AnexoTreeUtil.selecionarObjeto(anexoTree, anexo);
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
		anexoTree.clearSelection();
	}
}