package br.com.persist.plugins.checagem;

import java.awt.Color;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;

public class ChecagemCor {
	private static final Logger LOG = Logger.getGlobal();
	private final MutableAttributeSet attMagenta;
	private final MutableAttributeSet attBlue;
	private final MutableAttributeSet attRed2;
	private final MutableAttributeSet attRed;

	public ChecagemCor() {
		attMagenta = new SimpleAttributeSet();
		attBlue = new SimpleAttributeSet();
		attRed2 = new SimpleAttributeSet();
		attRed = new SimpleAttributeSet();
		StyleConstants.setForeground(attMagenta, Color.MAGENTA);
		StyleConstants.setBold(attMagenta, true);
		StyleConstants.setForeground(attBlue, Color.BLUE);
		StyleConstants.setForeground(attRed, Color.RED);
		StyleConstants.setBold(attRed, true);
		StyleConstants.setForeground(attRed2, new Color(180, 0, 0));
		StyleConstants.setBold(attRed2, true);
	}

	public void processar(StyledDocument doc, Modulo modulo) throws ChecagemException {
		try {
			doc.remove(0, doc.getLength());
			for (Bloco bloco : modulo.getBlocos()) {
				doc.insertString(doc.getLength(), "<set>" + Constantes.QL, attBlue);
				doc.insertString(doc.getLength(), "<![CDATA[", null);
				processar(doc, bloco);
				doc.insertString(doc.getLength(), "]]>" + Constantes.QL, null);
				doc.insertString(doc.getLength(), "</set>" + Constantes.QL, attBlue);
			}
		} catch (BadLocationException e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	private void processar(StyledDocument doc, Bloco bloco) throws ChecagemException, BadLocationException {
		ChecagemToken checagemToken = new ChecagemToken(bloco.getString(), true);
		List<Token> tokens = checagemToken.getTokens(true);
		for (Token token : tokens) {
			insert0(doc, token);
		}
	}

	private void insert0(StyledDocument doc, Token token) throws BadLocationException {
		if (token.isFuncaoInfixa() || token.isAuto()) {
			doc.insertString(doc.getLength(), token.getValor().toString(), attRed);
		} else if (token.isVariavel()) {
			doc.insertString(doc.getLength(), token.getValor().toString(), attMagenta);
		} else if (token.isString()) {
			doc.insertString(doc.getLength(), "'" + token.getValor().toString() + "'", attBlue);
		} else if (token.isDouble() || token.isLong()) {
			doc.insertString(doc.getLength(), token.getValor().toString(), attRed2);
		} else {
			doc.insertString(doc.getLength(), token.getValor().toString(), null);
		}
	}
}