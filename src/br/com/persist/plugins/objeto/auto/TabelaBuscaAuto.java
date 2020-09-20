package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.List;

public class TabelaBuscaAuto extends AbstratoTabela {
	private final List<BuscaAutoColetor> coletores;
	private boolean vazioInvisivel;
	private boolean processado;

	public TabelaBuscaAuto(String apelido, String nome, String campo) {
		super(apelido, nome, campo);
		coletores = new ArrayList<>();
	}

	public void inicializarColetores(List<String> numeros) {
		coletores.clear();
		for (String numero : numeros) {
			coletores.add(new BuscaAutoColetor(numero));
		}
	}

	public BuscaAutoColetor getColetor(String numero) {
		for (BuscaAutoColetor c : coletores) {
			if (c.getChave().equals(numero)) {
				return c;
			}
		}
		return null;
	}

	public void atualizarColetores(String numero) {
		for (BuscaAutoColetor c : coletores) {
			if (c.getChave().equals(numero)) {
				c.incrementarTotal();
			}
		}
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public boolean isProcessado() {
		return processado;
	}

	public boolean isVazioInvisivel() {
		return vazioInvisivel;
	}

	public void setVazioInvisivel(boolean vazioInvisivel) {
		this.vazioInvisivel = vazioInvisivel;
	}
}