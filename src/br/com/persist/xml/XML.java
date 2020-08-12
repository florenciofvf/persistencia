package br.com.persist.xml;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.persist.banco.Conexao;
import br.com.persist.fragmento.Fragmento;
import br.com.persist.modelo.MapeamentoModelo;
import br.com.persist.modelo.VariaveisModelo;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class XML {

	private XML() {
	}

	private static SAXParserFactory criarSAXParserFactory() throws XMLException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setNamespaceAware(true);
			factory.setXIncludeAware(true);
			return factory;
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public static void processar(File file, XMLColetor coletor) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			XMLHandler handler = new XMLHandler(coletor);
			parser.parse(file, handler);
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public static void processarConexao(File file, XMLColetor coletor) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			HandlerConn handler = new HandlerConn(coletor);
			parser.parse(file, handler);
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public static void processarFragmento(File file, XMLColetor coletor) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			HandlerFragmento handler = new HandlerFragmento(coletor);
			parser.parse(file, handler);
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public static void processarMapeamento(File file) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			parser.parse(file, new HandlerMapeamento());
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public static void processarVariaveis(File file) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			parser.parse(file, new HandlerVariaveis());
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}
}

class HandlerConn extends DefaultHandler {
	private final XMLColetor coletor;

	HandlerConn(XMLColetor coletor) {
		this.coletor = coletor;
		coletor.init();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("conexao".equals(qName)) {
			Conexao conexao = new Conexao();
			conexao.aplicar(attributes);
			coletor.getConexoes().add(conexao);
		}
	}
}

class HandlerFragmento extends DefaultHandler {
	private final XMLColetor coletor;

	HandlerFragmento(XMLColetor coletor) {
		this.coletor = coletor;
		coletor.init();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fragmento".equals(qName)) {
			Fragmento f = new Fragmento();
			f.aplicar(attributes);
			coletor.getFragmentos().add(f);
		}
	}
}

class HandlerMapeamento extends DefaultHandler {
	final StringBuilder builder = new StringBuilder();
	ChaveValor selecionado;

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (Constantes.CHAVE_VALOR.equals(qName)) {
			selecionado = new ChaveValor(Constantes.TEMP);
			selecionado.aplicar(attributes);
			MapeamentoModelo.adicionar(selecionado);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (Constantes.CHAVE_VALOR.equals(qName)) {
			selecionado = null;

		} else if (Constantes.VALOR.equals(qName) && selecionado != null) {
			String string = builder.toString();

			if (!Util.estaVazio(string)) {
				selecionado.setValor(string.trim());
			}

			limpar();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}

class HandlerVariaveis extends DefaultHandler {
	final StringBuilder builder = new StringBuilder();
	ChaveValor selecionado;

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (Constantes.CHAVE_VALOR.equals(qName)) {
			selecionado = new ChaveValor(Constantes.TEMP);
			selecionado.aplicar(attributes);
			VariaveisModelo.adicionar(selecionado);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (Constantes.CHAVE_VALOR.equals(qName)) {
			selecionado = null;

		} else if (Constantes.VALOR.equals(qName) && selecionado != null) {
			String string = builder.toString();

			if (!Util.estaVazio(string)) {
				selecionado.setValor(string.trim());
			}

			limpar();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}