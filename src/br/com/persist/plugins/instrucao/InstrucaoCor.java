package br.com.persist.plugins.instrucao;

import java.awt.Color;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import br.com.persist.plugins.instrucao.cmpl.Atom;

public class InstrucaoCor {
	private final MutableAttributeSet attMagenta;
	private final MutableAttributeSet attBlack;
	private final MutableAttributeSet attGreen;
	private final MutableAttributeSet attGray;
	private final MutableAttributeSet attBlue;
	private final MutableAttributeSet attRed2;
	private final MutableAttributeSet attRed;

	public InstrucaoCor() {
		attMagenta = new SimpleAttributeSet();
		attBlack = new SimpleAttributeSet();
		attGreen = new SimpleAttributeSet();
		attGray = new SimpleAttributeSet();
		attBlue = new SimpleAttributeSet();
		attRed2 = new SimpleAttributeSet();
		attRed = new SimpleAttributeSet();
		StyleConstants.setForeground(attGreen, new Color(0, 125, 0));
		StyleConstants.setForeground(attRed2, new Color(125, 0, 0));
		StyleConstants.setForeground(attMagenta, Color.MAGENTA);
		StyleConstants.setForeground(attBlack, Color.BLACK);
		StyleConstants.setForeground(attBlue, Color.BLUE);
		StyleConstants.setForeground(attGray, Color.GRAY);
		StyleConstants.setForeground(attRed, Color.RED);
		StyleConstants.setBold(attMagenta, true);
		StyleConstants.setBold(attBlack, true);
		StyleConstants.setBold(attGreen, true);
		StyleConstants.setBold(attRed2, true);
		StyleConstants.setBold(attRed, true);
	}

	public void processar(StyledDocument doc, List<Atom> atoms) {
		for (Atom atom : atoms) {
			if (stringReservada(atom.getValor())) {
				set(doc, atom, attRed2);
			} else if (atom.isVariavel()) {
				set(doc, atom, attMagenta);
			} else if (atom.isString()) {
				set(doc, atom, attBlue);
			} else if (atom.isParam()) {
				set(doc, atom, attGreen);
			}
		}
	}

	private boolean stringReservada(String string) {
		return InstrucaoConstantes.FUNCAO_NATIVA.equals(string) || InstrucaoConstantes.FUNCAO.equals(string)
				|| InstrucaoConstantes.IF.equals(string) || InstrucaoConstantes.VAR.equals(string)
				|| InstrucaoConstantes.VAL.equals(string);
	}

	private void set(StyledDocument doc, Atom atom, MutableAttributeSet att) {
		doc.setParagraphAttributes(atom.getIndice(), atom.getValor().length(), att, true);
	}
}