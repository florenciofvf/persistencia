package br.com.persist.plugins.persistencia;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

public class PersistenciaXMLHandler extends XMLHandler {
	private final List<List<Object>> registros = new ArrayList<>();
	private final StringBuilder builder = new StringBuilder();
	private List<Object> registro;
	private List<Coluna> colunas;
	private String tabela;

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	public String getTabela() {
		return tabela;
	}

	public List<Coluna> getColunas() {
		return colunas;
	}

	public List<List<Object>> getRegistros() {
		return registros;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("dados".equals(qName)) {
			tabela = attributes.getValue("tabela");
		} else if ("column".equals(qName)) {
			String nome = attributes.getValue("nome");
			String tipo = attributes.getValue("tipo");
			String chave = attributes.getValue("chave");
			String indice = attributes.getValue("indice");
			String numero = attributes.getValue("numero");
			Coluna coluna = new Coluna(nome, Integer.parseInt(indice), Boolean.parseBoolean(numero),
					Boolean.parseBoolean(chave), tipo);
			colunas.add(coluna);
		} else if ("head".equals(qName)) {
			colunas = new ArrayList<>();
		} else if ("row".equals(qName)) {
			registro = new ArrayList<>();
			registros.add(registro);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("cell".equals(qName)) {
			String string = builder.toString();
			registro.add(string.trim());
			limpar();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}