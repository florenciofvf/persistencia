package br.com.persist.plugins.robo;

import java.awt.Robot;
import java.util.Objects;

import br.com.persist.formulario.Formulario;

public abstract class Robo {
	protected static final int DELAY = 200;
	protected Formulario formulario;
	private final String nome;

	protected Robo(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public String getNome() {
		return nome;
	}

	abstract void processar(Robot robot, String[] params);
}