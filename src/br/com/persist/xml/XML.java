package br.com.persist.xml;

import java.io.File;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.util.Util;

public class XML {
	public static void processar(File file, List<Objeto> objetos, List<Relacao> relacoes) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setXIncludeAware(true);

		SAXParser parser = factory.newSAXParser();
		XMLHandler handler = new XMLHandler(objetos, relacoes);
		parser.parse(file, handler);
	}

	public static void processarConexao(File file, List<Conexao> conexoes) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();

		SAXParser parser = factory.newSAXParser();
		HandlerConn handler = new HandlerConn(conexoes);
		parser.parse(file, handler);
	}
}

class XMLHandler extends DefaultHandler {
	final StringBuilder builder = new StringBuilder();
	final List<Relacao> relacoes;
	final List<Objeto> objetos;
	Object selecionado;

	public XMLHandler(List<Objeto> objetos, List<Relacao> relacoes) {
		this.relacoes = relacoes;
		this.objetos = objetos;
	}

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("objeto".equals(qName)) {
			Objeto objeto = new Objeto();
			objeto.aplicar(attributes);
			selecionado = objeto;
			objetos.add(objeto);

		} else if ("relacao".equals(qName)) {
			Objeto objeto1 = getObjeto(attributes.getValue("objeto1"));
			Objeto objeto2 = getObjeto(attributes.getValue("objeto2"));

			boolean ponto1 = Boolean.parseBoolean(attributes.getValue("ponto1"));
			boolean ponto2 = Boolean.parseBoolean(attributes.getValue("ponto2"));

			Relacao relacao = new Relacao(objeto1, ponto1, objeto2, ponto2);
			relacao.aplicar(attributes);
			selecionado = relacao;
			relacoes.add(relacao);

		} else if ("desc".equals(qName)) {
			limpar();
		}
	}

	private Objeto getObjeto(String nome) {
		for (Objeto objeto : objetos) {
			if (nome.equals(objeto.getId())) {
				return objeto;
			}
		}

		throw new IllegalStateException();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("objeto".equals(qName) || "relacao".equals(qName)) {
			selecionado = null;

		} else if ("desc".equals(qName) && selecionado != null) {
			String string = builder.toString();

			if (!Util.estaVazio(string)) {
				if (selecionado instanceof Objeto) {
					Objeto obj = (Objeto) selecionado;
					obj.setDescricao(string.trim());

				} else if (selecionado instanceof Relacao) {
					Relacao rel = (Relacao) selecionado;
					rel.setDescricao(string.trim());
				}
			}

			limpar();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}

class HandlerConn extends DefaultHandler {
	final List<Conexao> conexoes;

	public HandlerConn(List<Conexao> conexoes) {
		this.conexoes = conexoes;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("conexao".equals(qName)) {
			Conexao conexao = new Conexao();
			conexao.aplicar(attributes);
			conexoes.add(conexao);
		}
	}
}