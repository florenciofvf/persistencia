package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;

public class ParaTabela {
	private final List<Instrucao> instrucoes;
	private String prefixoNomeTabela;
	private String selectAlternativo;
	private String ajustarAltura;
	private String finalConsulta;
	private String complemento;
	private String colunaInfo;
	private String destacavel;
	private String sequencias;
	private String campoNomes;
	private String mapeamento;
	private String linkAuto;
	private String apelido;
	private String orderBy;
	private String chaves;
	private String grupo;
	final Color corFonte;
	private String sane;
	private String ccsc;
	private String bpnt;
	final String tabela;
	final String icone;

	public ParaTabela(String tabela, String icone, Color corFonte) {
		if (Util.estaVazio(tabela)) {
			throw new IllegalStateException("Tabela vazia.");
		}
		instrucoes = new ArrayList<>();
		this.corFonte = corFonte;
		this.tabela = tabela;
		this.icone = icone;
	}

	public List<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public void add(Instrucao i) {
		if (i != null) {
			instrucoes.add(i);
		}
	}

	public void config(Objeto objeto) {
		if (!Util.estaVazio(ajustarAltura)) {
			objeto.setAjusteAutoForm(Boolean.parseBoolean(ajustarAltura));
		}
		if (!Util.estaVazio(colunaInfo)) {
			objeto.setColunaInfo(Boolean.parseBoolean(colunaInfo));
		}
		if (!Util.estaVazio(destacavel)) {
			objeto.setAbrirAuto(Boolean.parseBoolean(destacavel));
		}
		if (!Util.estaVazio(linkAuto)) {
			objeto.setLinkAuto(Boolean.parseBoolean(linkAuto));
		}
		if (!Util.estaVazio(prefixoNomeTabela)) {
			objeto.setPrefixoNomeTabela(prefixoNomeTabela);
		}
		if (!Util.estaVazio(selectAlternativo)) {
			objeto.setSelectAlternativo(selectAlternativo);
		}
		if (!Util.estaVazio(bpnt)) {
			objeto.setBpnt(Boolean.parseBoolean(bpnt));
		}
		if (!Util.estaVazio(finalConsulta)) {
			objeto.setFinalConsulta(finalConsulta);
		}
		if (!Util.estaVazio(apelido)) {
			objeto.setApelidoParaJoins(apelido);
		}
		config2(objeto);
	}

	private void config2(Objeto objeto) {
		if (!Util.estaVazio(sane)) {
			objeto.setSane(Boolean.parseBoolean(sane));
		}
		if (!Util.estaVazio(ccsc)) {
			objeto.setCcsc(Boolean.parseBoolean(ccsc));
		}
		if (!Util.estaVazio(complemento)) {
			objeto.setComplemento(complemento);
		}
		if (!Util.estaVazio(campoNomes)) {
			objeto.setChaveamento(campoNomes);
		}
		if (!Util.estaVazio(mapeamento)) {
			objeto.setMapeamento(mapeamento);
		}
		if (!Util.estaVazio(sequencias)) {
			objeto.setSequencias(sequencias);
		}
		if (corFonte != null) {
			objeto.setCorFonte(corFonte);
		}
		if (!Util.estaVazio(orderBy)) {
			objeto.setOrderBy(orderBy);
		}
		if (!Util.estaVazio(chaves)) {
			objeto.setChaves(chaves);
		}
		if (!Util.estaVazio(grupo)) {
			objeto.setGrupo(grupo);
		}
		if (icone != null) {
			objeto.setIcone(icone);
		}
	}

	public void modelo(XMLUtil util) {
		util.abrirTag("para").atributo(VinculoHandler.TABELA, VinculoHandler.NOME_TABELA)
				.atributo(VinculoHandler.ICONE, "nome_icone").atributo(VinculoHandler.COR_FONTE, "#FFVVFF").ql();
		util.tab().atributo("prefixoNomeTabela", "H_").ql();
		util.tab().atributo("selectAlternativo", "").ql();
		util.tab().atributo("finalConsulta", "").ql();
		util.tab().atributo("ajustarAltura", true).ql();
		util.tab().atributo("complemento", "").ql();
		util.tab().atributo("mapeamento", "").ql();
		util.tab().atributo("sequencias", "").ql();
		util.tab().atributo("campoNomes", "").ql();
		util.tab().atributo("colunaInfo", false).ql();
		util.tab().atributo("destacavel", true).ql();
		util.tab().atributo("linkAuto", true).ql();
		util.tab().atributo("apelido", "ape").ql();
		util.tab().atributo("orderBy", "").ql();
		util.tab().atributo("chaves", "").ql();
		util.tab().atributo(VinculoHandler.GRUPO, "").ql();
		util.tab().atributo("sane", true).ql();
		util.tab().atributo("ccsc", true).ql();
		util.tab().atributo("bpnt", false).fecharTag();
		Instrucao i = new Instrucao("Resumo da instrucao");
		i.setValor("UPDATE candidato SET votos = 0 WHERE id = #id#");
		i.salvar(util);
		util.finalizarTag("para");
	}

	public String getTabela() {
		return tabela;
	}

	public String getIcone() {
		return icone;
	}

	@Override
	public String toString() {
		return tabela;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ParaTabela) {
			ParaTabela outro = (ParaTabela) obj;
			return tabela.equals(outro.tabela);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return tabela.hashCode();
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public String getSequencias() {
		return sequencias;
	}

	public void setSequencias(String sequencias) {
		this.sequencias = sequencias;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getPrefixoNomeTabela() {
		return prefixoNomeTabela;
	}

	public void setPrefixoNomeTabela(String prefixoNomeTabela) {
		this.prefixoNomeTabela = prefixoNomeTabela;
	}

	public String getSelectAlternativo() {
		return selectAlternativo;
	}

	public void setSelectAlternativo(String selectAlternativo) {
		this.selectAlternativo = selectAlternativo;
	}

	public String getFinalConsulta() {
		return finalConsulta;
	}

	public void setFinalConsulta(String finalConsulta) {
		this.finalConsulta = finalConsulta;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getCampoNomes() {
		return campoNomes;
	}

	public void setCampoNomes(String campoNomes) {
		this.campoNomes = campoNomes;
	}

	public String getMapeamento() {
		return mapeamento;
	}

	public void setMapeamento(String mapeamento) {
		this.mapeamento = mapeamento;
	}

	public String getChaves() {
		return chaves;
	}

	public void setChaves(String chaves) {
		this.chaves = chaves;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Color getCorFonte() {
		return corFonte;
	}

	public String getAjustarAltura() {
		return ajustarAltura;
	}

	public void setAjustarAltura(String ajustarAltura) {
		this.ajustarAltura = ajustarAltura;
	}

	public String getColunaInfo() {
		return colunaInfo;
	}

	public void setColunaInfo(String colunaInfo) {
		this.colunaInfo = colunaInfo;
	}

	public String getDestacavel() {
		return destacavel;
	}

	public void setDestacavel(String destacavel) {
		this.destacavel = destacavel;
	}

	public String getLinkAuto() {
		return linkAuto;
	}

	public void setLinkAuto(String linkAuto) {
		this.linkAuto = linkAuto;
	}

	public String getSane() {
		return sane;
	}

	public void setSane(String sane) {
		this.sane = sane;
	}

	public String getCcsc() {
		return ccsc;
	}

	public void setCcsc(String ccsc) {
		this.ccsc = ccsc;
	}

	public String getBpnt() {
		return bpnt;
	}

	public void setBpnt(String bpnt) {
		this.bpnt = bpnt;
	}
}