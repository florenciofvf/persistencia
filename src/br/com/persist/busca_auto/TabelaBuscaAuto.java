package br.com.persist.busca_auto;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.util.Coletor;

public class TabelaBuscaAuto {
	private final List<Coletor> coletores;
	private final String apelidoTabelaCampo;
	private final String apelido;
	private final String campo;
	private boolean processado;
	private final String nome;

	public TabelaBuscaAuto(String apelidoTabelaCampo) {
		this.apelidoTabelaCampo = apelidoTabelaCampo;
		int pos = apelidoTabelaCampo.indexOf('.');
		String n = apelidoTabelaCampo.substring(0, pos);
		coletores = new ArrayList<>();

		if (n.startsWith("(")) {
			int pos2 = n.indexOf(')');
			apelido = n.substring(1, pos2);
			nome = n.substring(pos2 + 1);
		} else {
			apelido = "";
			nome = n;
		}

		campo = apelidoTabelaCampo.substring(pos + 1);
	}

	public void setNumeroColetores(List<String> numeros) {
		coletores.clear();

		if (numeros != null) {
			for (String numero : numeros) {
				coletores.add(new Coletor(numero));
			}
		}
	}

	public Coletor getColetor(String numero) {
		for (Coletor c : coletores) {
			if (c.getNumero().equals(numero)) {
				return c;
			}
		}

		return null;
	}

	public void checarTotal(String numero) {
		for (Coletor c : coletores) {
			if (c.getNumero().equals(numero)) {
				c.incrementarTotal();
			}
		}
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public String getApelidoTabelaCampo() {
		return apelidoTabelaCampo;
	}

	public boolean isProcessado() {
		return processado;
	}

	public String getApelido() {
		return apelido;
	}

	public String getCampo() {
		return campo;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return nome;
	}
}