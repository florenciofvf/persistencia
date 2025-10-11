package br.com.persist.componente;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.assistencia.Busca;
import br.com.persist.assistencia.Util;

public class FicharioPesquisa implements Busca {
	private List<Integer> lista = new ArrayList<>();
	private final AbstratoFichario fichario;
	final boolean porParte;
	final String string;
	int indice;

	public FicharioPesquisa(AbstratoFichario fichario, String string, boolean porParte) {
		this.fichario = Objects.requireNonNull(fichario);
		this.string = Objects.requireNonNull(string);
		this.porParte = porParte;
		inicializar();
	}

	private void inicializar() {
		indice = 0;
		List<Aba> abas = fichario.getAbas();
		for (Aba item : abas) {
			File file = item.getFile();
			if (file != null && Util.existeEm(file.getName(), string, porParte)) {
				lista.add(item.getIndice());
			}
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
			Integer aba = lista.get(indice);
			if (aba >= fichario.getTabCount()) {
				inicializar();
				return;
			}
			fichario.setSelectedIndex(aba);
			indice++;
			label.setText(indice + "/" + getTotal());
		} else {
			label.limpar();
			indice = 0;
		}
	}
}