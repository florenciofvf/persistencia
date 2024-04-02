package br.com.persist.plugins.robo;

import java.awt.Robot;
import java.util.Objects;

public abstract class Robo {
	private final String nome;

	protected Robo(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public String getNome() {
		return nome;
	}

	abstract void processar(Robot robot, String[] params);
}