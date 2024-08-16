package br.com.persist.plugins.mapa;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

public class MapaHandler extends XMLHandler {
	private final Set<Objeto> objetos = new HashSet<>();
	private Objeto selecionado;
	private Objeto raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (raiz == null) {
			raiz = new Objeto(qName);
			raiz.lerAtributos(attributes);
		} else if ("add".equals(qName)) {
			if (selecionado == null) {
				throw new SAXException("add deve possuir um parent.");
			}
			selecionado.adicionar(criarAdd(attributes));
		} else if ("ref".equals(qName)) {
			if (selecionado == null) {
				throw new SAXException("ref deve possuir um parent.");
			}
			selecionado.adicionar(criarRef(attributes));
		} else {
			selecionado = new Objeto(qName);
			selecionado.lerAtributos(attributes);
			objetos.add(selecionado);
		}
	}

	private Add criarAdd(Attributes attributes) {
		return new Add(attributes.getValue("obj"));
	}

	private Ref criarRef(Attributes attributes) {
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