package br.com.persist.plugins.propriedade;

import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class Campo extends Container {
	private final String nome;
	private final String valor;

	public Campo(String nome, String valor) {
		this.nome = Objects.requireNonNull(nome);
		this.valor = Objects.requireNonNull(valor);
	}

	public String getNome() {
		return nome;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}

	@Override
	public void color(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagSimples(PropriedadeConstantes.TAB3, "campo", doc);
		PropriedadeUtil.atributo("nome", nome, doc);
		PropriedadeUtil.atributo("valor", valor, doc);
		PropriedadeUtil.fimTagSimples(doc);
	}

	@Override
	public String toString() {
		return "Campo [nome=" + nome + ", valor=" + valor + "]";
	}
}