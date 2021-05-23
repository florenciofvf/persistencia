package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.Objeto;

public class Referencia {
	private final List<Coletor> coletores;
	private boolean validoInvisibilidade;
	private boolean vazioInvisivel;
	private final String tabela;
	private final String grupo;
	private final String campo;
	private boolean processado;
	private boolean limparApos;
	private String iconeGrupo;
	Pesquisa pesquisa;
	Color corFonte;
	String icone;

	public Referencia(String grupo, String tabela, String campo) {
		this.grupo = grupo == null ? "" : grupo.trim();
		this.campo = campo == null ? "" : campo.trim();
		coletores = new ArrayList<>();
		if (Util.estaVazio(tabela)) {
			throw new IllegalStateException("Tabela vazia.");
		}
		this.tabela = tabela;
	}

	public void config(Objeto objeto) {
		if (corFonte != null) {
			objeto.setCorFonte(corFonte);
		}
		if (icone != null) {
			objeto.setIcone(icone);
		}
	}

	public void descrever(boolean autonomo, StringBuilder builder) {
		builder.append(autonomo ? "<ref" : "");
		rotuloValor(builder, "tabela", tabela);
		rotuloValor(builder, "campo", campo);
		rotuloValor(builder, "grupo", grupo);
		if (limparApos) {
			rotuloValor(builder, "limparApos", "" + limparApos);
		}
		rotuloValor(builder, "icone", icone);
		if (vazioInvisivel) {
			rotuloValor(builder, "vazio", "invisivel");
		}
		if (corFonte != null) {
			rotuloValor(builder, "corFonte", toHex(corFonte));
		}
		rotuloValor(builder, "iconeGrupo", iconeGrupo);
		builder.append(autonomo ? "/>" : "");
	}

	static void rotuloValor(StringBuilder builder, String rotulo, String valor) {
		if (!Util.estaVazio(valor)) {
			builder.append(" ");
			builder.append(rotulo);
			builder.append("=");
			builder.append("\"");
			builder.append(valor);
			builder.append("\"");
		}
	}

	private String toHex(Color color) {
		StringBuilder sb = new StringBuilder("#");
		sb.append(Integer.toHexString(color.getRed()).toUpperCase());
		sb.append(Integer.toHexString(color.getGreen()).toUpperCase());
		sb.append(Integer.toHexString(color.getBlue()).toUpperCase());
		return sb.toString();
	}

	public boolean igual(Referencia ref) {
		return ref != null && grupo.equalsIgnoreCase(ref.grupo) && tabela.equalsIgnoreCase(ref.tabela)
				&& campo.equalsIgnoreCase(ref.campo);
	}

	public boolean igual2(Referencia ref) {
		return ref != null && grupo.equalsIgnoreCase(ref.grupo) && tabela.equalsIgnoreCase(ref.tabela);
	}

	public boolean igual(Objeto objeto) {
		return objeto != null && grupo.equalsIgnoreCase(objeto.getGrupo())
				&& tabela.equalsIgnoreCase(objeto.getTabela());
	}

	public Referencia clonar() {
		return new Referencia(grupo, tabela, campo);
	}

	public void inicializarColetores(List<String> numeros) {
		coletores.clear();
		for (String numero : numeros) {
			coletores.add(new Coletor(numero));
		}
	}

	public Coletor getColetor(String numero) {
		for (Coletor c : coletores) {
			if (c.getChave().equals(numero)) {
				return c;
			}
		}
		return null;
	}

	public void atualizarColetores(String numero) {
		for (Coletor c : coletores) {
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

	public Pesquisa getPesquisa() {
		return pesquisa;
	}

	public String getGrupo() {
		return grupo;
	}

	public String getTabela() {
		return tabela;
	}

	public String getCampo() {
		return campo;
	}

	public boolean isVazioInvisivel() {
		return vazioInvisivel;
	}

	public void setVazioInvisivel(boolean vazioInvisivel) {
		this.vazioInvisivel = vazioInvisivel;
	}

	public boolean isLimparApos() {
		return limparApos;
	}

	public void setLimparApos(boolean limparApos) {
		this.limparApos = limparApos;
	}

	public Color getCorFonte() {
		return corFonte;
	}

	public void setCorFonte(Color corFonte) {
		this.corFonte = corFonte;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public String getIconeGrupo() {
		return iconeGrupo;
	}

	public void setIconeGrupo(String iconeGrupo) {
		this.iconeGrupo = iconeGrupo;
	}

	public boolean isValidoInvisibilidade() {
		return validoInvisibilidade;
	}

	public void setValidoInvisibilidade(boolean validoInvisibilidade) {
		this.validoInvisibilidade = validoInvisibilidade;
	}

	@Override
	public String toString() {
		return "grupo=" + grupo + ", tabela=" + tabela + ", campo=" + campo;
	}

	public String toString2() {
		StringBuilder sb = new StringBuilder(tabela + "." + campo);
		if (!Util.estaVazio(grupo)) {
			sb.append(" GRUPO=" + grupo);
		}
		return sb.toString();
	}
}