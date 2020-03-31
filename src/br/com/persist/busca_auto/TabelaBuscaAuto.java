package br.com.persist.busca_auto;

import java.util.ArrayList;
import java.util.List;

public class TabelaBuscaAuto {
	private List<ContaBuscaAuto> liscaContaBuscaAuto;
	private final String apelidoTabelaCampo;
	private List<String> argumentos;
	private final String apelido;
	private final String campo;
	private boolean processado;
	private final String nome;

	public TabelaBuscaAuto(String apelidoTabelaCampo) {
		this.apelidoTabelaCampo = apelidoTabelaCampo;
		int pos = apelidoTabelaCampo.indexOf('.');
		String n = apelidoTabelaCampo.substring(0, pos);

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

	public void setArgumentos(List<String> argumentos) {
		this.argumentos = argumentos;

		if (argumentos != null) {
			liscaContaBuscaAuto = new ArrayList<>();

			for (String string : argumentos) {
				liscaContaBuscaAuto.add(new ContaBuscaAuto(string));
			}
		} else {
			liscaContaBuscaAuto = null;
		}
	}

	public ContaBuscaAuto getContaBuscaAuto(String tag) {
		if (liscaContaBuscaAuto == null) {
			return null;
		}

		for (ContaBuscaAuto c : liscaContaBuscaAuto) {
			if (c.tag.equals(tag)) {
				return c;
			}
		}

		return null;
	}

	public void contabilizar(String valor) {
		if (liscaContaBuscaAuto == null) {
			return;
		}

		for (ContaBuscaAuto c : liscaContaBuscaAuto) {
			if (c.tag.equals(valor)) {
				c.valor++;
			}
		}
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public String getApelidoTabelaCampo() {
		return apelidoTabelaCampo;
	}

	public List<String> getArgumentos() {
		return argumentos;
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