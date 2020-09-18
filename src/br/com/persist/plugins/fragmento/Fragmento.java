package br.com.persist.plugins.fragmento;

import br.com.persist.marca.XMLUtil;
import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class Fragmento {
	private final String resumo;
	private final String grupo;
	private String valor;

	public Fragmento(String resumo, String grupo) {
		if (Util.estaVazio(resumo)) {
			throw new IllegalArgumentException("Resumo nulo.");
		}
		if (Util.estaVazio(grupo)) {
			throw new IllegalArgumentException("Grupo nulo.");
		}
		this.resumo = resumo;
		this.grupo = grupo;
	}

	public Fragmento clonar() {
		Fragmento c = new Fragmento(resumo, grupo);
		c.valor = valor;
		return c;
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(Constantes.FRAGMENTO);
		util.atributo("resumo", Util.escapar(resumo));
		util.atributo("grupo", Util.escapar(grupo));
		util.fecharTag();

		util.abrirTag2(Constantes.VALOR);
		util.conteudo(Util.escapar(getValor())).ql();
		util.finalizarTag(Constantes.VALOR);

		util.finalizarTag(Constantes.FRAGMENTO);
	}

	public String getResumo() {
		return resumo;
	}

	public String getGrupo() {
		return grupo;
	}

	public boolean isValido() {
		return !Util.estaVazio(resumo) && !Util.estaVazio(grupo);
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		if (Util.estaVazio(valor)) {
			valor = Constantes.VAZIO;
		}

		return valor;
	}
}