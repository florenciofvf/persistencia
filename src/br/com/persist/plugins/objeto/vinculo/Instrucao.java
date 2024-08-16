package br.com.persist.plugins.objeto.vinculo;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.ObjetoException;

public class Instrucao implements Comparable<Instrucao> {
	private boolean selecaoMultipla;
	private boolean comoFiltro;
	private final String nome;
	private String valor;
	private int ordem;

	public Instrucao(String nome) throws ObjetoException {
		if (Util.isEmpty(nome)) {
			throw new ObjetoException("nome nulo.");
		}
		this.nome = nome;
	}

	public boolean isSelect() {
		return getValor().trim().toUpperCase().startsWith("SELECT");
	}

	public String getValor() {
		if (Util.isEmpty(valor)) {
			valor = Constantes.VAZIO;
		}
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getNome() {
		return nome;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public boolean isSelecaoMultipla() {
		return selecaoMultipla;
	}

	public void setSelecaoMultipla(boolean selecaoMultipla) {
		this.selecaoMultipla = selecaoMultipla;
	}

	public boolean isComoFiltro() {
		return comoFiltro;
	}

	public void setComoFiltro(boolean comoFiltro) {
		this.comoFiltro = comoFiltro;
	}

	public Instrucao clonar() throws ObjetoException {
		Instrucao i = new Instrucao(nome);
		i.setValor(valor);
		return i;
	}

	public void salvar(XMLUtil util, boolean ql) {
		if (!Util.isEmpty(getValor())) {
			if (ql) {
				util.ql();
			}
			util.abrirTag(VinculoHandler.INSTRUCAO);
			util.atributo("nome", nome);
			if (selecaoMultipla) {
				util.atributo("selecaoMultipla", selecaoMultipla);
			}
			if (comoFiltro) {
				util.atributo("comoFiltro", comoFiltro);
			}
			if (ordem != 0) {
				util.atributo("ordem", ordem);
			}
			util.fecharTag();
			util.conteudo("<![CDATA[").ql();
			util.tab().conteudo(getValor()).ql();
			util.conteudo("]]>").ql();
			util.finalizarTag(VinculoHandler.INSTRUCAO);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instrucao other = (Instrucao) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Instrucao o) {
		return ordem - o.ordem;
	}

	@Override
	public String toString() {
		return nome;
	}
}