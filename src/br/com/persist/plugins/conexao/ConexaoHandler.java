package br.com.persist.plugins.conexao;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.xml.XMLHandler;

class ConexaoHandler extends XMLHandler {
	private final ConexaoColetor coletor;

	ConexaoHandler(ConexaoColetor coletor) {
		this.coletor = coletor;
		coletor.init();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("conexao".equals(qName)) {
			Conexao conexao = new Conexao(attributes.getValue("nome"));
			conexao.aplicar(attributes);
			coletor.getConexoes().add(conexao);
		}
	}
}