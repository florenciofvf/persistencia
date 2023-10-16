package br.com.persist.plugins.objeto.vinculo;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Param {
	private final String chave;
	private final String rotulo;
	private String valor;

	public Param(String chave, String rotulo, String valor) {
		if (Util.estaVazio(chave)) {
			throw new IllegalStateException("Chave vazia.");
		}
		if (Util.estaVazio(rotulo)) {
			throw new IllegalStateException("Rotulo vazio.");
		}
		this.chave = chave;
		this.rotulo = rotulo;
		this.valor = valor;
	}

	public Param clonar() {
		return new Param(chave, rotulo, valor);
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getChave() {
		return chave;
	}

	public String getRotulo() {
		return rotulo;
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(VinculoHandler.PARAM);
		atributoValor(util, VinculoHandler.CHAVE, chave);
		atributoValor(util, VinculoHandler.ROTULO, rotulo);
		atributoValor(util, VinculoHandler.VALOR, valor);
		util.fecharTag(-1);
	}

	public void modelo(XMLUtil util) {
		util.abrirTag(VinculoHandler.PARAM).atributo(VinculoHandler.CHAVE, "unico_dentro_desta_pesquisa")
				.atributo(VinculoHandler.ROTULO, "Para dialogo valor dinamico")
				.atributo(VinculoHandler.VALOR, "Sem valor sera dinamico").fecharTag(-1);
	}

	public static void atributoValor(XMLUtil util, String nome, String valor) {
		Referencia.atributoValor(util, nome, valor);
	}
}