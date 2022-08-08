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
	private final MutableAttributeSet attBlack;
	private final MutableAttributeSet attGray;
	private final MutableAttributeSet attBlue;
	private final MutableAttributeSet attRed2;
	private final MutableAttributeSet attRed;

	public ChecagemCor() {
		attMagenta = new SimpleAttributeSet();
		attBlue = new SimpleAttributeSet();
		attBlack = new SimpleAttributeSet();
		attGray = new SimpleAttributeSet();
		attRed2 = new SimpleAttributeSet();
		attRed = new SimpleAttributeSet();
		StyleConstants.setForeground(attMagenta, Color.MAGENTA);
		StyleConstants.setBold(attMagenta, true);
		StyleConstants.setForeground(attBlack, Color.BLACK);
		StyleConstants.setForeground(attBlue, Color.BLUE);
		StyleConstants.setForeground(attGray, Color.GRAY);
		StyleConstants.setForeground(attRed, Color.RED);
		StyleConstants.setBold(attBlack, true);
		StyleConstants.setBold(attRed, true);
		StyleConstants.setForeground(attRed2, new Color(180, 0, 0));
		StyleConstants.setBold(attRed2, true);
	}

	public void processar(StyledDocument doc, Modulo modulo) throws ChecagemException {
		try {
			doc.remove(0, doc.getLength());
			for (Bloco bloco : modulo.getBlocos()) {
				insert(doc, "<set>", attBlue);
				insert(doc, bloco.getPreString(), attGray);
				processar(doc, bloco);
				insert(doc, bloco.getPosString(), attGray);
				insert(doc, "</set>" + Constantes.QL, attBlue);
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
			insert(doc, token.getValor().toString(), attRed);
		} else if (token.isVariavel()) {
			insert(doc, token.getValor().toString(), attMagenta);
		} else if (token.isVirgula()) {
			insert(doc, token.getValor().toString(), attBlack);
		} else if (token.isString()) {
			insert(doc, "'" + token.getValor().toString() + "'", attBlue);
		} else if (token.isDouble() || token.isLong()) {
			insert(doc, token.getValor().toString(), attRed2);
		} else {
			insert(doc, token.getValor().toString(), null);
		}
	}

	public void novaSentenca(StyledDocument doc) {
		try {
			if (doc.getLength() > 0 && !doc.getText(0, doc.getLength()).endsWith(Constantes.QL)) {
				insert(doc, Constantes.QL);
			}
			insert(doc, "<set>" + Constantes.QL, attBlue);
			insert(doc, "    <![CDATA[" + Constantes.QL);
			insert(doc, "        " + Constantes.QL);
			insert(doc, "    ]]>" + Constantes.QL);
			insert(doc, "</set>" + Constantes.QL, attBlue);
		} catch (BadLocationException e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	private void insert(StyledDocument doc, String string, MutableAttributeSet att) throws BadLocationException {
		doc.insertString(doc.getLength(), string, att);
	}

	private void insert(StyledDocument doc, String string) throws BadLocationException {
		insert(doc, string, null);
	}
}