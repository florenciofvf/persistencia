package br.com.persist.data;

public abstract class Tipo {
	Tipo pai;

	public Tipo getPai() {
		return pai;
	}

	public abstract void export(Container c, int tab);
}