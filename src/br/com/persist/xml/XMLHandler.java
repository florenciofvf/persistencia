package br.com.persist.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.persist.Instrucao;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Relacao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Util;

public class XMLHandler extends DefaultHandler {
	private final StringBuilder builder = new StringBuilder();
	private final XMLColetor coletor;
	private Object selecionado;

	public XMLHandler(XMLColetor coletor) {
		this.coletor = coletor;
		coletor.init();
	}

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fvf".equals(qName)) {
			coletor.getAjusteAutoForm().set(Boolean.parseBoolean(attributes.getValue("ajusteAutoForm")));
			coletor.getDimension().width = Integer.parseInt(attributes.getValue("largura"));
			coletor.getDimension().height = Integer.parseInt(attributes.getValue("altura"));

			String conexao = attributes.getValue("conexao");

			if (!Util.estaVazio(conexao)) {
				coletor.getSbConexao().append(conexao);
			}

		} else if ("objeto".equals(qName)) {
			Objeto objeto = new Objeto();
			objeto.aplicar(attributes);
			selecionado = objeto;
			coletor.getObjetos().add(objeto);

		} else if ("relacao".equals(qName)) {
			boolean pontoDestino = Boolean.parseBoolean(attributes.getValue("pontoDestino"));
			boolean pontoOrigem = Boolean.parseBoolean(attributes.getValue("pontoOrigem"));
			Objeto destino = getObjeto(attributes.getValue("destino"));
			Objeto origem = getObjeto(attributes.getValue("origem"));

			Relacao relacao = new Relacao(origem, pontoOrigem, destino, pontoDestino);
			relacao.aplicar(attributes);
			selecionado = relacao;
			coletor.getRelacoes().add(relacao);

		} else if ("form".equals(qName)) {
			Form f = new Form();
			f.aplicar(attributes);
			coletor.getForms().add(f);

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
		for (Objeto objeto : coletor.getObjetos()) {
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
}