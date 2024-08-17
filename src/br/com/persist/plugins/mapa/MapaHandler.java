package br.com.persist.plugins.mapa;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.marca.XMLHandler;

public class MapaHandler extends XMLHandler {
	private final Set<Objeto> objetos = new HashSet<>();
	private Objeto selecionado;
	private Objeto raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (raiz == null) {
			processRaiz(qName, attributes);
		} else if ("add".equals(qName)) {
			processAdd(attributes);
		} else if ("ref".equals(qName)) {
			processRef(attributes);
		} else {
			try {
				selecionado = new Objeto(qName);
				selecionado.lerAtributos(attributes);
				objetos.add(selecionado);
			} catch (ArgumentoException ex) {
				throw new SAXException(ex);
			}
		}
	}

	private void processRaiz(String qName, Attributes attributes) throws SAXException {
		try {
			raiz = new Objeto(qName);
			raiz.lerAtributos(attributes);
		} catch (ArgumentoException ex) {
			throw new SAXException(ex);
		}
	}

	private void processAdd(Attributes attributes) throws SAXException {
		if (selecionado == null) {
			throw new SAXException("add deve possuir um parent.");
		}
		try {
			selecionado.adicionar(criarAdd(attributes));
		} catch (ArgumentoException ex) {
			throw new SAXException(ex);
		}
	}

	private void processRef(Attributes attributes) throws SAXException {
		if (selecionado == null) {
			throw new SAXException("ref deve possuir um parent.");
		}
		try {
			selecionado.adicionar(criarRef(attributes));
		} catch (ArgumentoException ex) {
			throw new SAXException(ex);
		}
	}

	private Add criarAdd(Attributes attributes) throws ArgumentoException {
		return new Add(attributes.getValue("obj"));
	}

	private Ref criarRef(Attributes attributes) throws ArgumentoException {
		return new Ref(attributes.getValue("obj"));
	}

	public Objeto getRaiz() {
		return raiz;
	}

	public Objeto getObjeto(String nome) {
		for (Objeto objeto : objetos) {
			if (objeto.nome.equalsIgnoreCase(nome)) {
				return objeto;
			}
		}
		return null;
	}

	public Set<Objeto> getObjetos() {
		return objetos;
	}
}