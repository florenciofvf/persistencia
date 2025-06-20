package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.Objects;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoColetor;
import br.com.persist.plugins.objeto.ObjetoException;
import br.com.persist.plugins.objeto.Relacao;

public class RelacaoVinculo {
	private Color corFonte = Relacao.COR_PADRAO_FONTE;
	private Color cor = Relacao.COR_PADRAO;
	private int deslocamentoXDesc = -5;
	private int deslocamentoYDesc = -5;
	private boolean desenharDescricao;
	private final String destino;
	private boolean pontoDestino;
	private boolean pontoOrigem;
	private String chaveDestino;
	private final String origem;
	private boolean selecionado;
	private String chaveOrigem;
	private boolean processar;
	private boolean quebrado;
	private String descricao;

	public RelacaoVinculo(String origem, boolean pontoOrigem, String destino, boolean pontoDestino)
			throws ObjetoException {
		this.pontoDestino = pontoDestino;
		this.pontoOrigem = pontoOrigem;
		this.destino = Objects.requireNonNull(destino);
		this.origem = Objects.requireNonNull(origem);
		if (origem.equals(destino)) {
			throw new ObjetoException("origem e destino iguais");
		}
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setPontoDestino(boolean ponto) {
		this.pontoDestino = ponto;
	}

	public void setPontoOrigem(boolean ponto) {
		this.pontoOrigem = ponto;
	}

	public boolean isDesenharDescricao() {
		return desenharDescricao;
	}

	public void setDesenharDescricao(boolean desenharDescricao) {
		this.desenharDescricao = desenharDescricao;
	}

	public boolean isQuebrado() {
		return quebrado;
	}

	public void setQuebrado(boolean quebrado) {
		this.quebrado = quebrado;
	}

	public boolean isPontoDestino() {
		return pontoDestino;
	}

	public boolean isPontoOrigem() {
		return pontoOrigem;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = Constantes.VAZIO;
		}
		return descricao;
	}

	public String getDestino() {
		return destino;
	}

	public String getOrigem() {
		return origem;
	}

	public Color getCor() {
		return cor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RelacaoVinculo) {
			RelacaoVinculo outro = (RelacaoVinculo) obj;
			return (origem.equals(outro.origem) && destino.equals(outro.destino))
					|| (origem.equals(outro.destino) && destino.equals(outro.origem));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return origem.hashCode() + destino.hashCode();
	}

	public void aplicar(Attributes attr) {
		desenharDescricao = Boolean.parseBoolean(attr.getValue("desenharDescricao"));
		deslocamentoXDesc = Integer.parseInt(attr.getValue("desloc_x_desc"));
		deslocamentoYDesc = Integer.parseInt(attr.getValue("desloc_y_desc"));
		corFonte = new Color(Integer.parseInt(attr.getValue("corFonte")));
		processar = Boolean.parseBoolean(attr.getValue("processar"));
		quebrado = Boolean.parseBoolean(attr.getValue("quebrado"));
		cor = new Color(Integer.parseInt(attr.getValue("cor")));
		chaveDestino = attr.getValue("chaveDestino");
		chaveOrigem = attr.getValue("chaveOrigem");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("relacao");
		util.atributoCheck("origem", origem);
		util.atributoCheck("destino", destino);
		util.atributoCheck("chaveOrigem", getChaveOrigem());
		util.atributoCheck("chaveDestino", getChaveDestino());
		util.atributoCheck("desenharDescricao", desenharDescricao);
		util.atributo("desloc_x_desc", deslocamentoXDesc);
		util.atributo("desloc_y_desc", deslocamentoYDesc);
		util.atributo("corFonte", corFonte.getRGB());
		util.atributoCheck("pontoDestino", pontoDestino);
		util.atributoCheck("pontoOrigem", pontoOrigem);
		util.atributoCheck("processar", processar);
		util.atributoCheck("quebrado", quebrado);
		util.atributo("cor", cor.getRGB());
		util.fecharTag();
		if (!Util.isEmpty(getDescricao())) {
			util.abrirTag2("desc");
			util.conteudo("<![CDATA[").ql();
			util.tab().conteudo(getDescricao()).ql();
			util.conteudo("]]>").ql();
			util.finalizarTag("desc");
		}
		util.finalizarTag("relacao");
	}

	public Relacao criarRelacao(ObjetoColetor coletor) throws ObjetoException {
		Objeto objDestino = getObjeto(this.destino, coletor);
		Objeto objOrigem = getObjeto(this.origem, coletor);
		if (objOrigem == null || objDestino == null) {
			return null;
		}
		Relacao relacao = new Relacao(objOrigem, pontoOrigem, objDestino, pontoDestino);
		relacao.setDesenharDescricao(desenharDescricao);
		relacao.setDeslocamentoXDesc(deslocamentoXDesc);
		relacao.setDeslocamentoYDesc(deslocamentoYDesc);
		relacao.setChaveDestino(chaveDestino);
		relacao.setChaveOrigem(chaveOrigem);
		relacao.setProcessar(processar);
		relacao.setQuebrado(quebrado);
		relacao.setCorFonte(corFonte);
		relacao.setCor(cor);
		return relacao;
	}

	private Objeto getObjeto(String nome, ObjetoColetor coletor) {
		for (Objeto objeto : coletor.getObjetos()) {
			if (nome.equals(objeto.getId())) {
				return objeto;
			}
		}
		return null;
	}

	public int getDeslocamentoXDesc() {
		return deslocamentoXDesc;
	}

	public void setDeslocamentoXDesc(int deslocamentoXDesc) {
		this.deslocamentoXDesc = deslocamentoXDesc;
	}

	public void deslocamentoXDescDelta(int delta) {
		this.deslocamentoXDesc += delta;
	}

	public int getDeslocamentoYDesc() {
		return deslocamentoYDesc;
	}

	public void setDeslocamentoYDesc(int deslocamentoYDesc) {
		this.deslocamentoYDesc = deslocamentoYDesc;
	}

	public void deslocamentoYDescDelta(int delta) {
		this.deslocamentoYDesc += delta;
	}

	public String getChaveDestino() {
		if (Util.isEmpty(chaveDestino)) {
			chaveDestino = Constantes.VAZIO;
		}
		return chaveDestino;
	}

	public void setChaveDestino(String chaveDestino) {
		this.chaveDestino = chaveDestino;
	}

	public String getChaveOrigem() {
		if (Util.isEmpty(chaveOrigem)) {
			chaveOrigem = Constantes.VAZIO;
		}
		return chaveOrigem;
	}

	public void setChaveOrigem(String chaveOrigem) {
		this.chaveOrigem = chaveOrigem;
	}
}