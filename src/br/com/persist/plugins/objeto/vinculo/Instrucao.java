package br.com.persist.plugins.objeto.vinculo;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Instrucao implements Comparable<Instrucao> {
	private boolean selecaoMultipla;
	private final String nome;
	private String valor;
	private int ordem;

	public Instrucao(String nome) {
		if (Util.estaVazio(nome)) {
			throw new IllegalStateException();
		}
		this.nome = nome;
	}

	public boolean isSelect() {
		return getValor().trim().toUpperCase().startsWith("SELECT");
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

	public boolean isSelecaoMultipla() {
		return selecaoMultipla;
	}

	public void setSelecaoMultipla(boolean selecaoMultipla) {
		this.selecaoMultipla = selecaoMultipla;
	}

	public Instrucao clonar() {
		Instrucao i = new Instrucao(nome);
		i.setValor(valor);
		return i;
	}

	public void salvar(XMLUtil util) {
		if (!Util.estaVazio(getValor())) {
			util.abrirTag("instrucao");
			util.atributo("nome", Util.escapar(nome));
			util.atributo("ordem", ordem);
			util.atributo("selecaoMultipla", selecaoMultipla);
			util.fecharTag();
			util.abrirTag2(Constantes.VALOR);
			util.conteudo(Util.escapar(getValor())).ql();
			util.finalizarTag(Constantes.VALOR);
			util.finalizarTag("instrucao");
		}
	}

	public void modelo(XMLUtil util) {
		util.abrirTag(VinculoHandler.INSTRUCAO);
		util.atributo("nome", "Resumo da instrucao");
		util.atributo("selecaoMultipla", "false");
		util.atributo("ordem", "0").fecharTag();
		util.conteudo("<![CDATA[").ql();
		util.tab().conteudo("UPDATE candidato SET votos = 0 WHERE id = #id#").ql();
		util.conteudo("]]>").ql();
		util.finalizarTag(VinculoHandler.INSTRUCAO);
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