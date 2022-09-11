package br.com.persist.data;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class ContainerDocument implements Container {
	private static final Logger LOG = Logger.getGlobal();
	private final AbstractDocument doc;

	public ContainerDocument(AbstractDocument doc) {
		this.doc = Objects.requireNonNull(doc);
	}

	public AbstractDocument getAbstractDocument() {
		return doc;
	}

	@Override
	public String toString() {
		try {
			return doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			return "";
		}
	}

	@Override
	public void append(String string, AttributeSet attSet) {
		if (string != null) {
			try {
				doc.insertString(doc.getLength(), string, attSet);
			} catch (BadLocationException ex) {
				LOG.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}
}