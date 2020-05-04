package br.com.persist.xml;

import java.awt.Dimension;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.persist.Instrucao;
import br.com.persist.banco.Conexao;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Relacao;
import br.com.persist.exception.XMLException;
import br.com.persist.modelo.MapeamentoModelo;
import br.com.persist.modelo.VariaveisModelo;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Fragmento;
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

	public static Dimension processar(File file, List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms,
			StringBuilder sbConexao, AtomicBoolean ajusteAutoForm) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			XMLHandler handler = new XMLHandler(objetos, relacoes, forms, sbConexao, ajusteAutoForm);
			parser.parse(file, handler);
			return handler.getDimension();
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public static void processarConexao(File file, List<Conexao> conexoes) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			HandlerConn handler = new HandlerConn(conexoes);
			parser.parse(file, handler);
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public static void processarFragmento(File file, List<Fragmento> fragmentos) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			HandlerFragmento handler = new HandlerFragmento(fragmentos);
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

class XMLHandler extends DefaultHandler {
	final StringBuilder builder = new StringBuilder();
	final Dimension dimension = new Dimension();
	final AtomicBoolean ajusteAutoForm;
	final StringBuilder sbConexao;
	final List<Relacao> relacoes;
	final List<Objeto> objetos;
	final List<Form> forms;
	Object selecionado;

	XMLHandler(List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms, StringBuilder sbConexao,
			AtomicBoolean ajusteAutoForm) {
		this.ajusteAutoForm = ajusteAutoForm;
		this.sbConexao = sbConexao;
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
			ajusteAutoForm.set(Boolean.parseBoolean(attributes.getValue("ajusteAutoForm")));
			dimension.width = Integer.parseInt(attributes.getValue("largura"));
			dimension.height = Integer.parseInt(attributes.getValue("altura"));

			String conexao = attributes.getValue("conexao");

			if (!Util.estaVazio(conexao)) {
				sbConexao.append(conexao);
			}

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

		} else if ("instrucao".equals(qName)) {
			Instrucao i = new Instrucao(attributes.getValue("nome"));

			if (selecionado instanceof Objeto) {
				Objeto obj = (Objeto) selecionado;
				obj.addInstrucao(i);
			}

		} else if ("desc".equals(qName) || Constantes.VALOR.equals(qName)) {
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

		} else if (Constantes.VALOR.equals(qName) && selecionado != null) {
			String string = builder.toString();

			if (!Util.estaVazio(string) && selecionado instanceof Objeto) {
				Objeto obj = (Objeto) selecionado;
				obj.getUltInstrucao().setValor(string.trim());
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

	HandlerConn(List<Conexao> conexoes) {
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

	HandlerFragmento(List<Fragmento> fragmentos) {
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