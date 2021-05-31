package br.com.persist.componente;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import br.com.persist.assistencia.Constantes;

public class TextPane extends JTextPane {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getGlobal();

	public void append(String str) {
		Document doc = getDocument();
		if (doc != null) {
			try {
				doc.insertString(doc.getLength(), str, null);
			} catch (BadLocationException e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
	}

	public void limpar() {
		setText(Constantes.VAZIO);
	}
}