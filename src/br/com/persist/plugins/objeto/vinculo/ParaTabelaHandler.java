package br.com.persist.plugins.objeto.vinculo;

import java.util.List;
import java.util.Objects;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLHandler;
import br.com.persist.plugins.objeto.ObjetoException;

public class ParaTabelaHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private final ParaTabela paraTabela;

	public ParaTabelaHandler(ParaTabela paraTabela) {
		this.paraTabela = Objects.requireNonNull(paraTabela);
	}

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		try {
			if (VinculoHandler.INSTRUCAO.equals(qName)) {
				processarInstrucao(attributes);
				limpar();
			} else if (VinculoHandler.FILTRO.equals(qName)) {
				processarFiltro(attributes);
				limpar();
			}
		} catch (ObjetoException ex) {
			throw new SAXException(ex);
		}
	}

	private void processarInstrucao(Attributes attributes) throws ObjetoException {
		addInstrucao(attributes, paraTabela.getInstrucoes());
	}

	private void processarFiltro(Attributes attributes) throws ObjetoException {
		addFiltro(attributes, paraTabela.getFiltros());
	}

	private void addInstrucao(Attributes attributes, List<Instrucao> lista) throws ObjetoException {
		Instrucao i = new Instrucao(attributes.getValue(VinculoHandler.NOME));
		boolean sm = Boolean.parseBoolean(attributes.getValue("selecaoMultipla"));
		i.setSelecaoMultipla(sm);
		boolean cf = Boolean.parseBoolean(attributes.getValue("comoFiltro"));
		i.setComoFiltro(cf);
		String ordem = attributes.getValue(VinculoHandler.ORDEM);
		if (!Util.isEmpty(ordem)) {
			i.setOrdem(Integer.parseInt(ordem));
		}
		lista.add(i);
	}

	private void addFiltro(Attributes attributes, List<Filtro> lista) throws ObjetoException {
		Filtro f = new Filtro(attributes.getValue(VinculoHandler.NOME));
		String ordem = attributes.getValue(VinculoHandler.ORDEM);
		if (!Util.isEmpty(ordem)) {
			f.setOrdem(Integer.parseInt(ordem));
		}
		lista.add(f);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (VinculoHandler.INSTRUCAO.equals(qName)) {
			if (!paraTabela.getInstrucoes().isEmpty()) {
				setValorInstrucao(paraTabela.getInstrucoes());
			}
			limpar();
		} else if (VinculoHandler.FILTRO.equals(qName)) {
			if (!paraTabela.getFiltros().isEmpty()) {
				setValorFiltro(paraTabela.getFiltros());
			}
			limpar();
		}
	}

	private void setValorInstrucao(List<Instrucao> lista) {
		Instrucao instrucao = lista.get(lista.size() - 1);
		String string = builder.toString();
		if (!Util.isEmpty(string)) {
			instrucao.setValor(string.trim());
		}
	}

	private void setValorFiltro(List<Filtro> lista) {
		Filtro filtro = lista.get(lista.size() - 1);
		String string = builder.toString();
		if (!Util.isEmpty(string)) {
			filtro.setValor(string.trim());
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}