package br.com.persist.plugins.projeto;

import javax.swing.Icon;

public class ChaveIcone {
	final String chave;
	final Icon icone;

	public ChaveIcone(String chave, Icon icone) {
		this.chave = "_" + chave;
		this.icone = icone;
	}
}