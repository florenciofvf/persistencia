package br.com.persist.plugins.objeto.internal;

import java.util.List;

public class ArgumentoArray implements Argumento {
	private final List<Object[]> valoresChaves;
	private final int qtdChaves;

	public ArgumentoArray(List<Object[]> valoresChaves, int qtdChaves) {
		this.valoresChaves = valoresChaves;
		this.qtdChaves = qtdChaves;
	}

	public List<Object[]> getValoresChaves() {
		return valoresChaves;
	}

	public int getQtdChaves() {
		return qtdChaves;
	}
}