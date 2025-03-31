package br.com.persist.plugins.propriedade;

import java.awt.Color;
import java.io.ByteArrayInputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;

public class PropriedadeUtil {
	private static final MutableAttributeSet attGreen;
	private static final MutableAttributeSet attBlue;
	private static final MutableAttributeSet attRed;

	private PropriedadeUtil() {
	}

	public static Arvore criarRaiz(String string) throws XMLException {
		PropriedadeHandler handler = new PropriedadeHandler();
		XML.processar(new ByteArrayInputStream(string.getBytes()), handler);
		return handler.getRaiz();
	}

	static {
		attGreen = new SimpleAttributeSet();
		attBlue = new SimpleAttributeSet();
		attRed = new SimpleAttributeSet();
		StyleConstants.setForeground(attGreen, new Color(0, 125, 0));
		StyleConstants.setForeground(attBlue, Color.BLUE);
		StyleConstants.setForeground(attRed, Color.RED);
	}

	static void modulo(String tab, String nome, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), Constantes.QL + tab + "<!-- " + nome + " -->" + Constantes.QL, attBlue);
	}

	static void iniTagSimples(String tab, String nome, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), tab + "<" + nome, attGreen);
	}

	static void fimTagSimples(StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), "/>" + Constantes.QL, attGreen);
	}

	static void iniTagComposta(String tab, String nome, StyledDocument doc) throws BadLocationException {
		iniTagSimples(tab, nome, doc);
	}

	static void fimTagComposta(String tab, String nome, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), tab + "</" + nome + ">" + Constantes.QL, attGreen);
	}

	static void fimTagComposta(StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), ">" + Constantes.QL, attGreen);
	}

	static void atributoNome(String nome, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), " " + nome, attRed);
		doc.insertString(doc.getLength(), "=", null);
	}

	static void atributoValor(String str, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), XMLUtil.citar(str), attBlue);
	}

	static void atributo(String nome, String valor, StyledDocument doc) throws BadLocationException {
		PropriedadeUtil.atributoNome(nome, doc);
		PropriedadeUtil.atributoValor(valor, doc);
	}
}