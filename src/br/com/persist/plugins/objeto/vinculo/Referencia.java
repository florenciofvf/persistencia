package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoException;

public class Referencia {
	private final List<Coletor> coletores;
	private boolean validoInvisibilidade;
	private boolean vazioInvisivel;
	private final String tabela;
	private final String grupo;
	private final String campo;
	private boolean processado;
	private boolean limparApos;
	private String concatenar;
	Pesquisa pesquisa;

	public Referencia(String grupo, String tabela, String campo) throws ObjetoException {
		this.grupo = grupo == null ? "" : grupo.trim();
		this.campo = campo == null ? "" : campo.trim();
		coletores = new ArrayList<>();
		if (Util.isEmpty(tabela)) {
			throw new ObjetoException("Tabela vazia.");
		}
		this.tabela = tabela;
	}

	public Pesquisa rotuloDe(Objeto objeto) throws ObjetoException {
		if (objeto == null || pesquisa == null) {
			return null;
		}
		Pesquisa resp = new Pesquisa(objeto.getId(), clonar(), objeto.getIcone());
		resp.add(pesquisa.getReferencia().clonar());
		return resp;
	}

	public String[] getChavesArray() {
		if (Util.isEmpty(campo)) {
			return Constantes.ARRAY_LENGTH_ZERO;
		}
		return campo.trim().split(",");
	}

	public boolean isChaveMultipla() {
		return campo != null && campo.indexOf(',') != -1;
	}

	public void salvar(int indice, boolean autonomo, XMLUtil util) {
		if (autonomo) {
			if (isChaveMultipla()) {
				if (indice > 0) {
					util.ql();
				}
				util.conteudo("<!-- MAIS DE UMA CHAVE NESTE ITEM-->").ql();
			}
			util.abrirTag(VinculoHandler.REF);
		}
		atributoValor(util, VinculoHandler.TABELA, tabela);
		atributoValor(util, VinculoHandler.CAMPO, campo);
		atributoValor(util, VinculoHandler.GRUPO, grupo);
		if (limparApos) {
			atributoValor(util, VinculoHandler.LIMPAR_APOS, "" + limparApos);
		}
		if (vazioInvisivel) {
			atributoValor(util, VinculoHandler.VAZIO, VinculoHandler.INVISIVEL);
		}
		atributoValor(util, VinculoHandler.CONCATENAR, concatenar);
		if (autonomo) {
			util.fecharTag(-1);
		}
	}

	public String getConsulta() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT a.* FROM " + getTabela() + " a" + Constantes.QL);
		sb.append("   INNER JOIN " + pesquisa.getTabela() + " b ON b." + pesquisa.getCampo() + " = a." + getCampo()
				+ Constantes.QL);
		return sb.toString();
	}

	public void modelo(XMLUtil util) {
		util.abrirTag(VinculoHandler.REF).atributo(VinculoHandler.TABELA, VinculoHandler.NOME_TABELA)
				.atributo(VinculoHandler.CAMPO, "FK").atributo(VinculoHandler.GRUPO, "")
				.atributo(VinculoHandler.VAZIO, VinculoHandler.INVISIVEL).atributo(VinculoHandler.CONCATENAR, "")
				.fecharTag(-1);
	}

	public void modelo2(XMLUtil util) {
		util.abrirTag(VinculoHandler.REF).atributo(VinculoHandler.TABELA, VinculoHandler.NOME_TABELA)
				.atributo(VinculoHandler.LIMPAR_APOS, true).fecharTag(-1);
	}

	static void atributoValor(XMLUtil util, String nome, String valor) {
		if (!Util.isEmpty(valor)) {
			util.atributo(nome, valor);
		}
	}

	static String toHex(Color color) {
		StringBuilder sb = new StringBuilder("#");
		sb.append(toHexString(color.getRed()));
		sb.append(toHexString(color.getGreen()));
		sb.append(toHexString(color.getBlue()));
		return sb.toString();
	}

	static String toHexString(int i) {
		StringBuilder sb = new StringBuilder(Integer.toHexString(i));
		if (sb.length() == 1) {
			sb.insert(0, "0");
		}
		return sb.toString().toUpperCase();
	}

	public boolean igual(Referencia ref) {
		return ref != null && grupo.equalsIgnoreCase(ref.grupo) && tabela.equalsIgnoreCase(ref.tabela)
				&& campo.equalsIgnoreCase(ref.campo);
	}

	public boolean igual(Objeto objeto) {
		return objeto != null && grupo.equalsIgnoreCase(objeto.getGrupo())
				&& tabela.equalsIgnoreCase(objeto.getTabela());
	}

	public boolean coringa(Objeto objeto) {
		return objeto != null && grupo.equalsIgnoreCase(objeto.getGrupo()) && ehCoringa();
	}

	public boolean ehCoringa() {
		return "*".equals(tabela);
	}

	public Referencia clonar() throws ObjetoException {
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

	public void setVazioInvisivel() {
		this.vazioInvisivel = true;
	}

	public void setVazioVisivel() {
		this.vazioInvisivel = false;
	}

	public boolean isLimparApos() {
		return limparApos;
	}

	public void setLimparApos(boolean limparApos) {
		this.limparApos = limparApos;
	}

	public boolean isValidoInvisibilidade() {
		return validoInvisibilidade;
	}

	public void setValidoInvisibilidade(boolean validoInvisibilidade) {
		this.validoInvisibilidade = validoInvisibilidade;
	}

	@Override
	public String toString() {
		return "grupo=" + grupo + ", tabela=" + tabela + ", campo=" + campo + (limparApos ? ", limparApos=true" : "");
	}

	public String toString2() {
		StringBuilder sb = new StringBuilder(tabela + "." + campo);
		if (!Util.isEmpty(grupo)) {
			sb.append(" GRUPO=" + grupo);
		}
		return sb.toString();
	}

	public String getConcatenar(List<Param> lista) {
		String string = getConcatenar();
		if (!Util.isEmpty(string)) {
			for (Param param : lista) {
				string = Util.replaceAll(string, Constantes.SEP + param.getChave() + Constantes.SEP, param.getValor());
			}
		}
		return string;
	}

	public String getConcatenar() {
		if (Util.isEmpty(concatenar)) {
			return Constantes.VAZIO;
		}
		return " " + concatenar;
	}

	public void setConcatenar(String concatenar) {
		this.concatenar = concatenar;
	}
}