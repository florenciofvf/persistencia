package br.com.persist.plugins.objeto.vinculo;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Filtro implements Comparable<Filtro> {
	private final String nome;
	private String valor;
	private int ordem;

	public Filtro(String nome) {
		if (Util.estaVazio(nome)) {
			throw new IllegalStateException();
		}
		this.nome = nome;
	}

	public String getValor() {
		if (Util.estaVazio(valor)) {
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

	public Filtro clonar() {
		Filtro i = new Filtro(nome);
		i.setValor(valor);
		return i;
	}

	public void salvar(XMLUtil util, boolean ql) {
		if (!Util.estaVazio(getValor())) {
			if (ql) {
				util.ql();
			}
			util.abrirTag(VinculoHandler.FILTRO);
			util.atributo("nome", nome);
			if (ordem != 0) {
				util.atributo("ordem", ordem);
			}
			util.fecharTag();
			util.conteudo("<![CDATA[").ql();
			util.tab().conteudo(getValor()).ql();
			util.conteudo("]]>").ql();
			util.finalizarTag(VinculoHandler.FILTRO);
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
		Filtro other = (Filtro) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Filtro o) {
		return ordem - o.ordem;
	}

	@Override
	public String toString() {
		return nome;
	}
}