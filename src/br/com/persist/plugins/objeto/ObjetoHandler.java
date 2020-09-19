package br.com.persist.plugins.objeto;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLHandler;
import br.com.persist.plugins.objeto.internal.InternalForm;

public class ObjetoHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private final ObjetoColetor coletor;
	private Object selecionado;

	public ObjetoHandler(ObjetoColetor coletor) {
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
			InternalForm f = new InternalForm();
			f.aplicar(attributes);
			coletor.getForms().add(f);

		} else if ("instrucao".equals(qName)) {
			Instrucao i = new Instrucao(attributes.getValue("nome"));

			String ordem = attributes.getValue("ordem");

			if (!Util.estaVazio(ordem)) {
				i.setOrdem(Integer.parseInt(ordem));
			}

			if (selecionado instanceof Objeto) {
				Objeto obj = (Objeto) selecionado;
				obj.addInstrucao(i);
			}

		} else if ("desc".equals(qName) || Constantes.VALOR.equals(qName) || "buscaAutomatica".equals(qName)) {
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
			setDescricao();

		} else if ("buscaAutomatica".equals(qName) && selecionado != null) {
			setBuscaAutomatica();

		} else if (Constantes.VALOR.equals(qName) && selecionado != null) {
			String string = builder.toString();

			if (!Util.estaVazio(string) && selecionado instanceof Objeto) {
				Objeto obj = (Objeto) selecionado;
				obj.getUltInstrucao().setValor(string.trim());
			}

			limpar();
		}
	}

	private void setDescricao() {
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

	private void setBuscaAutomatica() {
		String string = builder.toString();

		if (!Util.estaVazio(string) && selecionado instanceof Objeto) {
			Objeto obj = (Objeto) selecionado;
			obj.setBuscaAutomatica(string.trim());
		}

		limpar();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}