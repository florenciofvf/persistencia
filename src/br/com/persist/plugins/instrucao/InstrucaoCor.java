package br.com.persist.plugins.instrucao;

import java.awt.Color;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import br.com.persist.plugins.instrucao.compilador.Token;

public class InstrucaoCor {
	private static final MutableAttributeSet MAGENTA = new SimpleAttributeSet();
	private static final MutableAttributeSet GREEN2 = new SimpleAttributeSet();
	private static final MutableAttributeSet GREEN3 = new SimpleAttributeSet();
	private static final MutableAttributeSet BLUE2 = new SimpleAttributeSet();
	private static final MutableAttributeSet GRAY = new SimpleAttributeSet();
	public static final MutableAttributeSet PLAIN = new SimpleAttributeSet();
	private static final MutableAttributeSet BLUE = new SimpleAttributeSet();
	private static final MutableAttributeSet BOLD = new SimpleAttributeSet();
	private static final MutableAttributeSet RED2 = new SimpleAttributeSet();
	private static final MutableAttributeSet RED = new SimpleAttributeSet();
	private static final MutableAttributeSet TAG = new SimpleAttributeSet();

	private InstrucaoCor() {
	}

	public static void processar(StyledDocument doc, List<Token> tokens) {
		for (Token token : tokens) {
			if (token.isIgnorarCor() || token.getIndice() < 0) {
				continue;
			}
			if (token.isReservado()) {
				set(doc, token, RED);
			} else if (token.isConstante()) {
				set(doc, token, BLUE2);
			} else if (token.isParametro() || token.isLista()) {
				set(doc, token, GREEN2);
			} else if (token.isMapa()) {
				set(doc, token, GREEN3);
			} else if (token.isFuncao()) {
				set(doc, token, MAGENTA);
			} else if (token.isString()) {
				set(doc, token, BLUE);
			} else if (token.isComentario()) {
				set(doc, token, GRAY);
			} else if (token.isEspecial()) {
				set(doc, token, BOLD);
			} else if (token.isNumero()) {
				set(doc, token, RED2);
			}
		}
	}

	static void set(StyledDocument doc, Token token, MutableAttributeSet att) {
		if (token.getIndice2() > token.getIndice()) {
			doc.setCharacterAttributes(token.getIndice(), token.getIndice2() - token.getIndice(), att, true);
		} else {
			doc.setCharacterAttributes(token.getIndice(), token.getString().length(), att, true);
		}
	}

	public static void clearAttr(StyledDocument doc) {
		doc.setCharacterAttributes(0, doc.getLength(), PLAIN, true);
	}

	static {
		StyleConstants.setForeground(GRAY, new Color(192, 192, 192));
		StyleConstants.setForeground(GREEN3, new Color(45, 100, 47));
		StyleConstants.setBackground(TAG, new Color(225, 225, 225));
		StyleConstants.setForeground(GREEN2, new Color(0, 125, 0));
		StyleConstants.setForeground(BLUE2, new Color(0, 0, 125));
		StyleConstants.setForeground(RED, new Color(130, 0, 83));
		StyleConstants.setForeground(BLUE, new Color(0, 0, 255));
		StyleConstants.setForeground(RED2, new Color(255, 0, 0));
		StyleConstants.setForeground(MAGENTA, Color.MAGENTA);
		StyleConstants.setBold(MAGENTA, true);
		StyleConstants.setBold(GREEN2, true);
		StyleConstants.setBold(GREEN3, true);
		StyleConstants.setBold(BLUE2, true);
		StyleConstants.setBold(GRAY, true);
		StyleConstants.setBold(BLUE, true);
		StyleConstants.setBold(BOLD, true);
		StyleConstants.setBold(RED2, true);
		StyleConstants.setBold(RED, true);
	}
}