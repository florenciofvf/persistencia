package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class TabelaBuscaAuto {
	private final List<BuscaAutoColetor> coletores;
	private final String tabelasApos;
	private final String apelido;
	private final String campo;
	private boolean processado;
	private final String nome;

	public TabelaBuscaAuto(String apelidoTabelaCampo, String contextoDebug) {
		int pos = apelidoTabelaCampo.indexOf('.');
		checarPos(pos, "SEM CAMPO DEFINIDO NA BUSCA AUTO -> " + contextoDebug + " > " + apelidoTabelaCampo);
		String[] arrayApelidoTabela = separarApelidoTabela(apelidoTabelaCampo.substring(0, pos));
		String stringCampo = apelidoTabelaCampo.substring(pos + 1).trim();
		String[] arrayCampoTabelasApos = extrairCampoTabelasApos(stringCampo);
		tabelasApos = arrayCampoTabelasApos[1];
		campo = arrayCampoTabelasApos[0];
		apelido = arrayApelidoTabela[0];
		coletores = new ArrayList<>();
		nome = arrayApelidoTabela[1];
		checarCampo(campo);
	}

	private String[] extrairCampoTabelasApos(String campo) {
		String[] strings = new String[2];

		int pos = campo.indexOf('[');

		if (pos == -1) {
			if (campo.endsWith("]")) {
				campo = campo.substring(0, campo.length() - 1);
			}

			strings[0] = campo;
			strings[1] = Constantes.VAZIO;

			return strings;
		}

		if (!campo.endsWith("]")) {
			throw new IllegalStateException("Tabela apos sem -> ]");
		}

		strings[0] = campo.substring(0, pos);
		strings[1] = campo.substring(pos + 1, campo.length() - 1);

		return strings;
	}

	public String getTabelasApos() {
		return tabelasApos;
	}

	public String getApelidoTabelaCampo() {
		return getApelidoTabelaCampo(apelido, nome, campo)
				+ (Util.estaVazio(tabelasApos) ? Constantes.VAZIO : "[" + tabelasApos + "]");
	}

	public static String getApelidoTabelaCampo(String apelido, String nome, String campo) {
		if (Util.estaVazio(apelido)) {
			return nome + "." + campo;
		}

		return "(" + apelido + ")" + nome + "." + campo;
	}

	public static void checarPos(int pos, String msg) {
		if (pos < 0) {
			throw new IllegalStateException(msg);
		}
	}

	public static void checarCampo(String campo) {
		if (Util.estaVazio(campo)) {
			throw new IllegalStateException("Nome do campo vazio");
		}
	}

	public boolean igual(TabelaBuscaAuto tabela) {
		return apelido.equals(tabela.apelido) && nome.equals(tabela.nome);
	}

	public static String[] separarApelidoTabela(String string) {
		String[] strings = new String[2];

		if (Util.estaVazio(string)) {
			throw new IllegalArgumentException("separarApelidoTabela");
		}

		string = string.trim();

		if (string.startsWith("(")) {
			int pos2 = string.indexOf(')');

			if (pos2 == -1) {
				throw new IllegalArgumentException("separarApelidoTabela falta fechar com -> )");
			}

			strings[0] = string.substring(1, pos2).trim();
			strings[1] = string.substring(pos2 + 1).trim();
		} else {
			strings[0] = Constantes.VAZIO;
			strings[1] = string;
		}

		if (Util.estaVazio(strings[1])) {
			throw new IllegalStateException("Nome da tabela vazio");
		}

		return strings;
	}

	public void setNumeroColetores(List<String> numeros) {
		coletores.clear();

		if (numeros != null) {
			for (String numero : numeros) {
				coletores.add(new BuscaAutoColetor(numero));
			}
		}
	}

	public BuscaAutoColetor getColetor(String numero) {
		for (BuscaAutoColetor c : coletores) {
			if (c.getNumero().equals(numero)) {
				return c;
			}
		}

		return null;
	}

	public void checarColetores(String numero) {
		for (BuscaAutoColetor c : coletores) {
			if (c.getNumero().equals(numero)) {
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