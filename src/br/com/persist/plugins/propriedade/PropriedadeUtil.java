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
	private static final MutableAttributeSet attPink;
	private static final String TAB = "\t\t";

	private PropriedadeUtil() {
	}

	public static Raiz criarRaiz(String string) throws XMLException {
		PropriedadeHandler handler = new PropriedadeHandler();
		XML.processar(new ByteArrayInputStream(string.getBytes()), handler);
		return handler.getRaiz();
	}

	static {
		attGreen = new SimpleAttributeSet();
		attBlue = new SimpleAttributeSet();
		attPink = new SimpleAttributeSet();
		StyleConstants.setForeground(attGreen, new Color(0, 125, 0));
		StyleConstants.setForeground(attBlue, Color.BLUE);
		StyleConstants.setForeground(attPink, Color.RED);
	}

	static void bloco(String nome, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), Constantes.QL + TAB + "<!-- " + nome + " -->" + Constantes.QL, attBlue);
	}

	static void iniPropriedade(String nome, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), TAB + "<" + nome, attGreen);
	}

	static void atributo(String nome, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), " " + nome, attPink);
		doc.insertString(doc.getLength(), "=", null);
	}

	static void valorAtr(String str, StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), XMLUtil.citar(str), attBlue);
	}

	static void fimPropriedade(StyledDocument doc) throws BadLocationException {
		doc.insertString(doc.getLength(), "/>" + Constantes.QL, attGreen);
	}
}