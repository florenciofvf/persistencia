package br.com.persist.plugins.expressao;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Expressao {
	private String nome;
	private String valor;

	public Expressao() throws ArgumentoException {
		this(null);
	}

	public Expressao(String nome) throws ArgumentoException {
		this.nome = nome;
	}

	public Expressao clonar(String novoNome) throws ArgumentoException {
		Expressao obj = new Expressao(novoNome);
		obj.setValor(valor);
		return obj;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		if (Util.isEmpty(valor)) {
			valor = Constantes.VAZIO;
		}
		return valor;
	}

	public boolean isValido() {
		return !Util.isEmpty(nome);
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(ExpressaoConstantes.EXPRESSOES);
		util.atributo("nome", nome);
		util.fecharTag();
		util.abrirTag2(Constantes.VALOR);
		util.conteudo("<![CDATA[").ql();
		util.tab().conteudo(getValor()).ql();
		util.conteudo("]]>").ql();
		util.finalizarTag(Constantes.VALOR);
		util.finalizarTag(ExpressaoConstantes.EXPRESSOES);
	}
}
