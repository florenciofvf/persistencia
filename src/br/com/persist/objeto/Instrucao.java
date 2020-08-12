package br.com.persist.objeto;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;
import br.com.persist.xml.XMLUtil;

public class Instrucao {
	private final String nome;
	private String valor;

	public Instrucao(String nome) {
		if (Util.estaVazio(nome)) {
			throw new IllegalStateException();
		}

		this.nome = nome;
	}

	public boolean isSelect() {
		return getValor().trim().toUpperCase().startsWith("SELECT");
	}

	public String getValor() {
		if (Util.estaVazio(valor)) {
			valor = Constantes.VAZIO;
		}

		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getNome() {
		return nome;
	}

	public Instrucao clonar() {
		Instrucao i = new Instrucao(nome);
		i.setValor(valor);

		return i;
	}

	public void salvar(XMLUtil util) {
		if (!Util.estaVazio(getValor())) {
			util.abrirTag("instrucao");
			util.atributo("nome", Util.escapar(nome));
			util.fecharTag();

			util.abrirTag2(Constantes.VALOR);
			util.conteudo(Util.escapar(getValor())).ql();
			util.finalizarTag(Constantes.VALOR);

			util.finalizarTag("instrucao");
		}
	}
}