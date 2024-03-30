package br.com.persist.geradores;

public abstract class ObjetoString extends Objeto {
	protected String string;

	protected ObjetoString(String id, String string) {
		super(id);
		this.string = string;
	}

	@Override
	public String toString() {
		return super.toString() + ":" + string;
	}
}