package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLHandler;
import br.com.persist.plugins.objeto.ObjetoException;

public class VinculoHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	public static final String NOME_TABELA = "NOME_TABELA";
	public static final String LIMPAR_APOS = "limparApos";
	public static final String ICONE_GRUPO = "iconeGrupo";
	private final Map<String, ParaTabela> mapaParaTabela;
	public static final String CONCATENAR = "concatenar";
	public static final String INVISIVEL = "invisivel";
	public static final String INSTRUCAO = "instrucao";
	public static final String PESQUISA = "pesquisa";
	public static final String RELACAO = "relacao";
	public static final String ROTULO = "rotulo";
	public static final String FILTRO = "filtro";
	public static final String TABELA = "tabela";
	private final List<RelacaoVinculo> relacoes;
	public static final String CAMPO = "campo";
	public static final String VALOR = "valor";
	public static final String ICONE = "icone";
	public static final String GRUPO = "grupo";
	public static final String VAZIO = "vazio";
	public static final String PARAM = "param";
	public static final String CHAVE = "chave";
	public static final String ORDEM = "ordem";
	public static final String NOME = "nome";
	public static final String PARA = "para";
	public static final String REF = "ref";
	public final List<Pesquisa> pesquisas;
	private String tabelaSelecionada;
	private Pesquisa selecionado;

	public VinculoHandler() {
		mapaParaTabela = new LinkedHashMap<>();
		pesquisas = new ArrayList<>();
		relacoes = new ArrayList<>();
	}

	public Map<String, ParaTabela> getMapaParaTabela() {
		return mapaParaTabela;
	}

	public List<RelacaoVinculo> getRelacoes() {
		return relacoes;
	}

	public List<Pesquisa> getPesquisas() {
		return pesquisas;
	}

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		try {
			processStartElement(qName, attributes);
		} catch (ObjetoException ex) {
			throw new SAXException(ex);
		}
	}

	private void processStartElement(String qName, Attributes attributes) throws ObjetoException {
		if (PESQUISA.equals(qName)) {
			selecionado = new Pesquisa(attributes.getValue(NOME), criar(attributes), attributes.getValue(ICONE_GRUPO));
			String ordem = attributes.getValue(ORDEM);
			if (!Util.isEmpty(ordem)) {
				selecionado.setOrdem(Integer.parseInt(ordem));
			}
			if (!PesquisaUtil.contem(selecionado, pesquisas)) {
				pesquisas.add(selecionado);
			}
		} else if (PARA.equals(qName)) {
			tabelaSelecionada = attributes.getValue(TABELA);
			if (!Util.isEmpty(tabelaSelecionada)) {
				ParaTabela paraTabela = criarParaTabela(tabelaSelecionada, attributes);
				mapaParaTabela.computeIfAbsent(tabelaSelecionada, t -> paraTabela);
			}
		}
		processStartElement2(qName, attributes);
	}

	private void processStartElement2(String qName, Attributes attributes) throws ObjetoException {
		if (INSTRUCAO.equals(qName)) {
			processarInstrucao(attributes);
			limpar();
		} else if (FILTRO.equals(qName)) {
			processarFiltro(attributes);
			limpar();
		} else if (REF.equals(qName) && selecionado != null) {
			selecionado.add(criar(attributes));
		} else if (PARAM.equals(qName) && selecionado != null) {
			selecionado.add(criarParam(attributes));
		} else if (RELACAO.equals(qName)) {
			processarRelacao(attributes);
		} else if ("desc".equals(qName)) {
			limpar();
		}
	}

	private void processarRelacao(Attributes attributes) throws ObjetoException {
		boolean pontoDestino = Boolean.parseBoolean(attributes.getValue("pontoDestino"));
		boolean pontoOrigem = Boolean.parseBoolean(attributes.getValue("pontoOrigem"));
		String destino = attributes.getValue("destino");
		String origem = attributes.getValue("origem");
		RelacaoVinculo relacao = new RelacaoVinculo(origem, pontoOrigem, destino, pontoDestino);
		relacao.aplicar(attributes);
		relacoes.add(relacao);
	}

	private void processarInstrucao(Attributes attributes) throws ObjetoException {
		ParaTabela paraTabela = mapaParaTabela.get(tabelaSelecionada);
		if (paraTabela != null) {
			addInstrucao(attributes, paraTabela.getInstrucoes());
		}
	}

	private void processarFiltro(Attributes attributes) throws ObjetoException {
		ParaTabela paraTabela = mapaParaTabela.get(tabelaSelecionada);
		if (paraTabela != null) {
			addFiltro(attributes, paraTabela.getFiltros());
		}
	}

	private void addInstrucao(Attributes attributes, List<Instrucao> lista) throws ObjetoException {
		Instrucao i = new Instrucao(attributes.getValue(NOME));
		boolean sm = Boolean.parseBoolean(attributes.getValue("selecaoMultipla"));
		i.setSelecaoMultipla(sm);
		boolean cf = Boolean.parseBoolean(attributes.getValue("comoFiltro"));
		i.setComoFiltro(cf);
		String ordem = attributes.getValue(ORDEM);
		if (!Util.isEmpty(ordem)) {
			i.setOrdem(Integer.parseInt(ordem));
		}
		lista.add(i);
	}

	private void addFiltro(Attributes attributes, List<Filtro> lista) throws ObjetoException {
		Filtro f = new Filtro(attributes.getValue(NOME));
		String ordem = attributes.getValue(ORDEM);
		if (!Util.isEmpty(ordem)) {
			f.setOrdem(Integer.parseInt(ordem));
		}
		lista.add(f);
	}

	private static ParaTabela criarParaTabela(String tabela, Attributes attributes) throws ObjetoException {
		ParaTabela paraTabela = new ParaTabela(tabela);
		paraTabela.aplicar(attributes);
		return paraTabela;
	}

	public static Param criarParam(Attributes attributes) throws ObjetoException {
		return new Param(attributes.getValue(CHAVE), attributes.getValue(ROTULO), attributes.getValue(VALOR));
	}

	public static Referencia criar(Attributes attributes) throws ObjetoException {
		Referencia ref = new Referencia(attributes.getValue(GRUPO), attributes.getValue(TABELA),
				attributes.getValue(CAMPO));
		ref.setVazioInvisivel(INVISIVEL.equalsIgnoreCase(attributes.getValue(VAZIO)));
		String limparApos = attributes.getValue(LIMPAR_APOS);
		ref.setLimparApos(Boolean.parseBoolean(limparApos));
		ref.setConcatenar(attributes.getValue(CONCATENAR));
		return ref;
	}

	public static Referencia criar(Map<String, String> attributes) throws ObjetoException {
		Referencia ref = new Referencia(attributes.get(GRUPO), attributes.get(TABELA), attributes.get(CAMPO));
		ref.setVazioInvisivel(INVISIVEL.equalsIgnoreCase(attributes.get(VAZIO)));
		String limparApos = attributes.get(LIMPAR_APOS);
		ref.setConcatenar(attributes.get(CONCATENAR));
		ref.setLimparApos(Boolean.parseBoolean(limparApos));
		return ref;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (PESQUISA.equals(qName)) {
			selecionado = null;
		} else if (INSTRUCAO.equals(qName)) {
			ParaTabela paraTabela = mapaParaTabela.get(tabelaSelecionada);
			if (paraTabela != null && !paraTabela.getInstrucoes().isEmpty()) {
				setValorInstrucao(paraTabela.getInstrucoes());
			}
			limpar();
		} else if (FILTRO.equals(qName)) {
			ParaTabela paraTabela = mapaParaTabela.get(tabelaSelecionada);
			if (paraTabela != null && !paraTabela.getFiltros().isEmpty()) {
				setValorFiltro(paraTabela.getFiltros());
			}
			limpar();
		} else if ("desc".equals(qName)) {
			setDescricao();
		}
	}

	private void setDescricao() {
		if (relacoes.isEmpty()) {
			limpar();
			return;
		}
		String string = builder.toString();
		if (!Util.isEmpty(string)) {
			RelacaoVinculo item = relacoes.get(relacoes.size() - 1);
			item.setDescricao(string.trim());
		}
		limpar();
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