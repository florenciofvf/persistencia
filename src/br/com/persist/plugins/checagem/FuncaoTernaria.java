package br.com.persist.plugins.checagem;

public abstract class FuncaoTernaria extends FuncaoBinariaOuMaior {

	public FuncaoTernaria() {
		super(3);
	}

	public Sentenca param2() {
		return parametros.get(2);
	}
}