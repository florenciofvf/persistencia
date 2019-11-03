package br.com.persist.util;

import java.util.Objects;

import org.xml.sax.Attributes;

import br.com.persist.xml.XMLUtil;

public class ChaveValor {
	private String chave;
	private String valor;

	public ChaveValor(String chave) {
		this(chave, null);
	}

	public ChaveValor(String chave, String valor) {
		Objects.requireNonNull(chave);
		this.chave = chave;
		this.valor = valor;
	}

	public ChaveValor clonar() {
		ChaveValor cv = new ChaveValor(chave);
		cv.valor = valor;
		return cv;
	}

	public void aplicar(Attributes attr) {
		chave = attr.getValue("chave");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("chave_valor");
		util.atributo("chave", Util.escapar(chave));
		util.fecharTag();

		util.abrirTag2(Constantes.VALOR);
		util.conteudo(Util.escapar(getValor())).ql();
		util.finalizarTag(Constantes.VALOR);

		util.finalizarTag("chave_valor");
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public boolean isValida() {
		return !Util.estaVazio(chave) && !Util.estaVazio(valor);
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		if (Util.estaVazio(valor)) {
			valor = "";
		}

		return valor;
	}

	public int getInteiro(int padrao) {
		if (Util.estaVazio(valor)) {
			return padrao;
		}

		try {
			return Integer.parseInt(valor.trim());
		} catch (Exception e) {
			return padrao;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
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

		ChaveValor other = (ChaveValor) obj;

		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return chave;
	}
}