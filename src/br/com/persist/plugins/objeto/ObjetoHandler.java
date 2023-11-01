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
			processarRaiz(attributes);
		} else if ("objeto".equals(qName)) {
			processarObjeto(attributes);
		} else if ("relacao".equals(qName)) {
			processarRelacao(attributes);
		} else if ("form".equals(qName)) {
			processarForm(attributes);
		} else if ("desc".equals(qName) || Constantes.VALOR.equals(qName) || "buscaAutomatica".equals(qName)) {
			limpar();
		}
	}

	private void processarForm(Attributes attributes) {
		InternalForm f = new InternalForm();
		f.aplicar(attributes);
		coletor.getForms().add(f);
	}

	private void processarRelacao(Attributes attributes) {
		boolean pontoDestino = Boolean.parseBoolean(attributes.getValue("pontoDestino"));
		boolean pontoOrigem = Boolean.parseBoolean(attributes.getValue("pontoOrigem"));
		Objeto destino = getObjeto(attributes.getValue("destino"));
		Objeto origem = getObjeto(attributes.getValue("origem"));
		Relacao relacao = new Relacao(origem, pontoOrigem, destino, pontoDestino);
		relacao.aplicar(attributes);
		selecionado = relacao;
		coletor.getRelacoes().add(relacao);
	}

	private void processarObjeto(Attributes attributes) {
		Objeto objeto = new Objeto();
		objeto.aplicar(attributes);
		selecionado = objeto;
		coletor.getObjetos().add(objeto);
	}

	private void processarRaiz(Attributes attributes) {
		coletor.getAjusteLarguraForm().set(Boolean.parseBoolean(attributes.getValue("ajusteLarguraForm")));
		coletor.getAjusteAutoForm().set(Boolean.parseBoolean(attributes.getValue("ajusteAutoForm")));
		coletor.getProcessar().set(Boolean.parseBoolean(attributes.getValue("processar")));
		coletor.getDimension().width = Integer.parseInt(attributes.getValue("largura"));
		coletor.getDimension().height = Integer.parseInt(attributes.getValue("altura"));
		coletor.setArquivoVinculo(attributes.getValue("arquivoVinculo"));
		String conexao = attributes.getValue("conexao");
		if (!Util.isEmpty(conexao)) {
			coletor.getSbConexao().append(conexao);
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
		}
	}

	private void setDescricao() {
		String string = builder.toString();
		if (!Util.isEmpty(string)) {
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

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}