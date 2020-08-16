package br.com.persist.instrucao;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;
import br.com.persist.xml.XMLUtil;

public class Instrucao implements Comparable<Instrucao> {
	private String nome;
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

	public void setNome(String nome) {
		if (Util.estaVazio(nome)) {
			return;
		}

		this.nome = nome;
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
			util.fecharTag();

			util.abrirTag2(Constantes.VALOR);
			util.conteudo(Util.escapar(getValor())).ql();
			util.finalizarTag(Constantes.VALOR);

			util.finalizarTag("instrucao");
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
}