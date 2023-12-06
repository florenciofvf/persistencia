package br.com.persist.plugins.execucao;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIDefaults;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;
import br.com.persist.componente.TextPane;

public class GitCor implements EditorCor {
	public static final MutableAttributeSet PLAIN = new SimpleAttributeSet();
	private static final Color COLOR_EDITOR = new Color(0, 119, 65);
	private static final Logger LOG = Logger.getGlobal();
	private final MutableAttributeSet attYellow;
	private final MutableAttributeSet attWhite;
	private final MutableAttributeSet attGreen;
	private final MutableAttributeSet attCyan;
	private final MutableAttributeSet attRed;

	public GitCor() {
		attYellow = new SimpleAttributeSet();
		attWhite = new SimpleAttributeSet();
		attGreen = new SimpleAttributeSet();
		attCyan = new SimpleAttributeSet();
		attRed = new SimpleAttributeSet();
		StyleConstants.setForeground(attWhite, new Color(246, 234, 169));
		StyleConstants.setForeground(attYellow, new Color(225, 175, 81));
		StyleConstants.setForeground(attGreen, new Color(65, 207, 96));
		StyleConstants.setForeground(attCyan, new Color(85, 225, 236));
		StyleConstants.setForeground(attRed, new Color(99, 21, 16));
	}

	public void processar(TextPane textPane, StringBuilder sb) {
		if (sb == null || sb.length() == 0) {
			return;
		}
		StyledDocument doc = textPane.getStyledDocument();
		UIDefaults defaults = new UIDefaults();
		defaults.put("TextPane[Enabled].backgroundPainter", COLOR_EDITOR);
		textPane.putClientProperty("Nimbus.Overrides", defaults);
		textPane.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
		textPane.setBackground(COLOR_EDITOR);
		try {
			List<String> list = listString(sb);
			for (String string : list) {
				String str = string.trim();
				if (str.startsWith("---") || str.startsWith("+++") || str.startsWith("diff --git")) {
					insert(doc, string, attYellow);

				} else if (str.startsWith("@@")) {
					insert(doc, string, attCyan);

				} else if (str.startsWith("-") || str.startsWith("modified:")) {
					insert(doc, string, attRed);

				} else if (str.startsWith("+")) {
					insert(doc, string, attGreen);

				} else {
					insert(doc, string, attWhite);
				}
			}
		} catch (BadLocationException e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	private List<String> listString(StringBuilder sb) {
		List<String> resp = new ArrayList<>();
		Token token = new Token(sb.toString());
		String str = token.proximo();
		while (str != null) {
			resp.add(str);
			str = token.proximo();
		}
		return resp;
	}

	private void insert(StyledDocument doc, String string, MutableAttributeSet att) throws BadLocationException {
		doc.insertString(doc.getLength(), string + Constantes.QL, att);
	}
}

class Token {
	final String string;
	int indice;

	public Token(String string) {
		this.string = string;
	}

	String proximo() {
		if (indice >= string.length()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			indice++;
			if (c == '\n') {
				break;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}