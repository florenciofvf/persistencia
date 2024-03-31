package br.com.persist.geradores;

public class Arquivo extends Container {
	public Arquivo() {
		super("Arquivo");
	}

	public Container addPackage(String string) {
		add(new Package(string));
		return this;
	}
}