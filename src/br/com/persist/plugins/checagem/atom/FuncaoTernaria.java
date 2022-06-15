package br.com.persist.plugins.checagem.atom;

public abstract class FuncaoTernaria extends FuncaoBinariaOuMaior {

	public FuncaoTernaria() {
		super(3);
	}

	public Sentenca param2() {
		return parametros.get(2);
	}
}