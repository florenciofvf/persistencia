package br.com.persist.marca;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class XMLHandler extends DefaultHandler implements LexicalHandler {
	@Override
	public void startDTD(String name, String publicId, String systemId) throws SAXException {
	}

	@Override
	public void endDTD() throws SAXException {
	}

	@Override
	public void startEntity(String name) throws SAXException {
	}

	@Override
	public void endEntity(String name) throws SAXException {
	}

	@Override
	public void startCDATA() throws SAXException {
	}

	@Override
	public void endCDATA() throws SAXException {
	}

	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
	}
}