package br.com.persist.plugins.propriedade;

import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.xml.sax.Attributes;

public class Campo extends Container {
	private static final String ATT_VALOR = "valor";
	public static final String TAB_CAMPO = "campo";
	private static final String ATT_NOME = "nome";
	private final String valor;
	private final String nome;
	private boolean invalido;

	public Campo(String nome, String valor) {
		this.nome = Objects.requireNonNull(nome);
		this.valor = Objects.requireNonNull(valor);
	}

	public static Campo criar(Attributes atts) {
		Campo campo = new Campo(value(atts, ATT_NOME), value(atts, ATT_VALOR));
		String string = value(atts, Modulo.ATT_INVALIDO);
		campo.setInvalido(Modulo.TRUE.equalsIgnoreCase(string));
		return campo;
	}

	public String getNome() {
		return nome;
	}

	public String getValor() {
		return valor;
	}

	public boolean isInvalido() {
		return invalido;
	}

	public void setInvalido(boolean invalido) {
		this.invalido = invalido;
	}

	@Override
	public void adicionar(Container c) throws PropriedadeException {
		throw new PropriedadeException("erro.container_vazio");
	}

	@Override
	public void print(StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.iniTagSimples(PropriedadeConstantes.TAB3, TAB_CAMPO, doc);
		PropriedadeUtil.atributo(ATT_NOME, nome, doc);
		PropriedadeUtil.atributo(ATT_VALOR, valor, doc);
		if (invalido) {
			PropriedadeUtil.atributo(Modulo.ATT_INVALIDO, Modulo.TRUE, doc);
		}
		PropriedadeUtil.fimTagSimples(doc);
	}

	@Override
	public String toString() {
		return simpleName() + " [" + ATT_NOME + "=" + nome + ", " + ATT_VALOR + "=" + valor + "]";
	}
}