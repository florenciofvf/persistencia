package br.com.persist.geradores;

public class Arquivo extends ContainerJV {
	public Arquivo() {
		super("Arquivo");
	}

	public Container addPackage(String string) {
		add(new Package(string));
		return this;
	}
}