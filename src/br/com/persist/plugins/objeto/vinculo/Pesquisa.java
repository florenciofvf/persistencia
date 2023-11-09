package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;

public class Pesquisa {
	private final List<Referencia> referenciasApos;
	private final List<Referencia> referencias;
	private final List<Param> cloneParams;
	private final Referencia referencia;
	private final List<Param> params;
	private Objeto objeto;
	private String nome;

	public Pesquisa(String nome, Referencia ref) {
		this.referencia = Objects.requireNonNull(ref);
		if (Util.isEmpty(nome)) {
			throw new IllegalStateException("Nome da pesquisa vazia.");
		}
		referenciasApos = new ArrayList<>();
		referencias = new ArrayList<>();
		cloneParams = new ArrayList<>();
		params = new ArrayList<>();
		this.nome = nome;
	}

	public void setNome(String nome) {
		if (!Util.isEmpty(nome)) {
			this.nome = nome;
		}
	}

	public Pesquisa inverter(String nome, Objeto objeto) {
		return objeto == null ? inverter(nome) : inverter2(nome, objeto);
	}

	private Pesquisa inverter(String nome) {
		if (referencias.size() != 1) {
			return null;
		}
		Referencia ref = referencias.get(0);
		nome = nome == null ? referencia.getTabela() : nome;
		Pesquisa resp = new Pesquisa(nome, ref.clonar());
		resp.add(referencia.clonar());
		return resp;
	}

	private Pesquisa inverter2(String nome, Objeto objeto) {
		for (Referencia ref : referencias) {
			if (ref.igual(objeto)) {
				Pesquisa resp = new Pesquisa(nome, ref.clonar());
				resp.add(referencia.clonar());
				return resp;
			}
		}
		return null;
	}

	public void processar(Objeto objeto) {
		if (referencia.igual(objeto)) {
			objeto.addPesquisa(this);
			objeto.addReferencias(referencias);
			referencia.config(objeto);
		}
		for (Referencia ref : referencias) {
			if (ref.igual(objeto)) {
				objeto.addReferencia(ref.getPesquisa().referencia);
				ref.config(objeto);
			}
		}
	}

	public boolean igual(Objeto objeto) {
		return referencia.igual(objeto);
	}

	public boolean ehEquivalente(Pesquisa pesquisa, Objeto objeto) {
		return pesquisa != null && nome.equalsIgnoreCase(pesquisa.nome) && referencia.igual(objeto)
				&& pesquisa.referencia.igual(objeto);
	}

	public boolean igual(Pesquisa pesquisa) {
		return pesquisa != null && nome.equalsIgnoreCase(pesquisa.nome) && referencia.igual(pesquisa.referencia);
	}

	public void salvar(XMLUtil util, boolean ql) {
		if (ql) {
			util.ql();
		}
		if (referencia.isChaveMultipla()) {
			util.conteudo("<!-- MAIS DE UMA CHAVE-PRIMARIA NESTA PESQUISA-->").ql();
		}
		util.abrirTag(VinculoHandler.PESQUISA).atributo(VinculoHandler.NOME, nome);
		referencia.salvar(0, false, util);
		util.fecharTag();
		for (Param par : params) {
			par.salvar(util);
		}
		int indice = 0;
		for (Referencia ref : referencias) {
			ref.salvar(indice, true, util);
			indice++;
		}
		for (Referencia ref : referenciasApos) {
			ref.salvar(indice, true, util);
			indice++;
		}
		util.finalizarTag(VinculoHandler.PESQUISA);
	}

	public String getConsulta() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT a.* FROM " + getTabela() + " a" + Constantes.QL);
		int i = 0;
		for (Referencia ref : referencias) {
			sb.append("   INNER JOIN " + ref.getTabela() + (" o_" + (++i)) + " ON a." + getCampo() + " = "
					+ ("o_" + i + ".") + ref.getCampo() + Constantes.QL);
		}
		return sb.toString();
	}

	public String getConsultaReversa() {
		StringBuilder sb = new StringBuilder();
		for (Referencia ref : referencias) {
			sb.append(ref.getConsulta() + Constantes.QL);
		}
		return sb.toString();
	}

	public Objeto getObjeto() {
		return objeto;
	}

	public void setObjeto(Objeto objeto) {
		this.objeto = objeto;
	}

	public String getTabela() {
		return referencia.getTabela();
	}

	public String getCampo() {
		return referencia.getCampo();
	}

	public void modelo(XMLUtil util) {
		util.ql();
		util.abrirTag(VinculoHandler.PESQUISA).atributo(VinculoHandler.NOME, "Nome da pesquisa")
				.atributo(VinculoHandler.TABELA, VinculoHandler.NOME_TABELA).atributo(VinculoHandler.CAMPO, "PK")
				.atributo(VinculoHandler.GRUPO, "").atributo(VinculoHandler.ICONE_GRUPO, "")
				.atributo(VinculoHandler.ICONE, "").atributo(VinculoHandler.COR_FONTE, "#AABBCC").fecharTag();
		new Param(".", ".", null).modelo(util);
		new Referencia(null, ".", null).modelo(util);
		new Referencia(null, ".", null).modelo2(util);
		util.finalizarTag(VinculoHandler.PESQUISA);
	}

	public String getNome() {
		return nome;
	}

	public Referencia getReferencia() {
		return referencia;
	}

	public void inicializarColetores(List<String> numeros) {
		for (Referencia ref : referencias) {
			ref.inicializarColetores(numeros);
		}
	}

	public void validoInvisibilidade(boolean b) {
		for (Referencia ref : referencias) {
			ref.setValidoInvisibilidade(b);
		}
	}

	public void setProcessado(boolean b) {
		for (Referencia ref : referencias) {
			ref.setProcessado(b);
		}
	}

	public void setVazioInvisivel() {
		for (Referencia ref : referencias) {
			ref.setVazioInvisivel();
		}
	}

	public void setVazioVisivel() {
		for (Referencia ref : referencias) {
			ref.setVazioVisivel();
		}
	}

	public boolean isProcessado() {
		for (Referencia ref : referencias) {
			if (ref.isProcessado()) {
				return true;
			}
		}
		return false;
	}

	public List<Referencia> getReferencias() {
		return referencias;
	}

	public List<Referencia> getReferenciasApos() {
		return referenciasApos;
	}

	public void add(Param param) {
		if (param != null) {
			params.add(param);
		}
	}

	public void clonarParams() {
		cloneParams.clear();
		for (Param param : getParams()) {
			cloneParams.add(param.clonar());
		}
	}

	public List<Param> getParams() {
		return params;
	}

	public List<Param> getCloneParams() {
		return cloneParams;
	}

	public boolean add(Referencia ref) {
		if (ref != null) {
			if (!ref.isLimparApos() && !contem(ref, referencias)) {
				referencias.add(ref);
				ref.pesquisa = this;
				return true;
			} else if (ref.isLimparApos() && !contem(ref, referenciasApos)) {
				referenciasApos.add(ref);
				return true;
			}
		}
		return false;
	}

	public boolean contemLimparResto() {
		for (Referencia ref : referenciasApos) {
			if (ref.ehCoringa()) {
				return true;
			}
		}
		return false;
	}

	public boolean addLimparResto() {
		Referencia ref = new Referencia(null, "*", null);
		ref.setLimparApos(true);
		return add(ref);
	}

	public void excluirLimparResto() {
		Iterator<Referencia> it = referenciasApos.iterator();
		while (it.hasNext()) {
			Referencia ref = it.next();
			if (ref.ehCoringa()) {
				it.remove();
			}
		}
	}

	public void addRef(Map<String, String> map) {
		add(VinculoHandler.criar(map));
	}

	public void add(List<Referencia> referencias) {
		for (Referencia ref : referencias) {
			add(ref);
		}
	}

	public static boolean contem(Pesquisa pesquisa, List<Pesquisa> pesquisas) {
		for (Pesquisa pesq : pesquisas) {
			if (pesq.nome.equalsIgnoreCase(pesquisa.nome) && pesq.referencia.igual(pesquisa.referencia)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contem(Referencia ref, List<Referencia> referencias) {
		for (Referencia r : referencias) {
			if (r.igual(ref)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contem2(Referencia ref, List<Referencia> referencias) {
		for (Referencia r : referencias) {
			if (r.igual2(ref)) {
				return true;
			}
		}
		return false;
	}

	public String getNomeParaMenuItem() {
		return nome + " - " + referencia.getCampo();
	}

	@Override
	public String toString() {
		return "nome=" + nome + ", ref=" + referencia;
	}
}