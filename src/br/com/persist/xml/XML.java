package br.com.persist.xml;

import java.awt.Dimension;
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
import br.com.persist.util.Form;
import br.com.persist.util.Fragmento;
import br.com.persist.util.Util;

public class XML {
	public static Dimension processar(File file, List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms)
			throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setXIncludeAware(true);

		SAXParser parser = factory.newSAXParser();
		XMLHandler handler = new XMLHandler(objetos, relacoes, forms);
		parser.parse(file, handler);
		return handler.getDimension();
	}

	public static void processarConexao(File file, List<Conexao> conexoes) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();

		SAXParser parser = factory.newSAXParser();
		HandlerConn handler = new HandlerConn(conexoes);
		parser.parse(file, handler);
	}

	public static void processarFragmento(File file, List<Fragmento> fragmentos) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();

		SAXParser parser = factory.newSAXParser();
		HandlerFragmento handler = new HandlerFragmento(fragmentos);
		parser.parse(file, handler);
	}
}

class XMLHandler extends DefaultHandler {
	private final Dimension dimension = new Dimension();
	final StringBuilder builder = new StringBuilder();
	final List<Relacao> relacoes;
	final List<Objeto> objetos;
	final List<Form> forms;
	Object selecionado;

	public XMLHandler(List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms) {
		this.relacoes = relacoes;
		this.objetos = objetos;
		this.forms = forms;
	}

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fvf".equals(qName)) {
			dimension.width = Integer.parseInt(attributes.getValue("largura"));
			dimension.height = Integer.parseInt(attributes.getValue("altura"));

		} else if ("objeto".equals(qName)) {
			Objeto objeto = new Objeto();
			objeto.aplicar(attributes);
			selecionado = objeto;
			objetos.add(objeto);

		} else if ("relacao".equals(qName)) {
			boolean pontoDestino = Boolean.parseBoolean(attributes.getValue("pontoDestino"));
			boolean pontoOrigem = Boolean.parseBoolean(attributes.getValue("pontoOrigem"));
			Objeto destino = getObjeto(attributes.getValue("destino"));
			Objeto origem = getObjeto(attributes.getValue("origem"));

			Relacao relacao = new Relacao(origem, pontoOrigem, destino, pontoDestino);
			relacao.aplicar(attributes);
			selecionado = relacao;
			relacoes.add(relacao);

		} else if ("form".equals(qName)) {
			Form f = new Form();
			f.aplicar(attributes);
			forms.add(f);

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

	public Dimension getDimension() {
		return dimension;
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

class HandlerFragmento extends DefaultHandler {
	final List<Fragmento> fragmentos;

	public HandlerFragmento(List<Fragmento> fragmentos) {
		this.fragmentos = fragmentos;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fragmento".equals(qName)) {
			Fragmento f = new Fragmento();
			f.aplicar(attributes);
			fragmentos.add(f);
		}
	}
}