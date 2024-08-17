package br.com.persist.plugins.fragmento;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Fragmento {
	private final String resumo;
	private final String grupo;
	private String valor;

	public Fragmento(String resumo, String grupo) throws ArgumentoException {
		if (Util.isEmpty(resumo)) {
			throw new ArgumentoException("Resumo nulo.");
		}
		if (Util.isEmpty(grupo)) {
			throw new ArgumentoException("Grupo nulo.");
		}
		this.resumo = resumo;
		this.grupo = grupo;
	}

	public Fragmento clonar(String novoResumo) throws ArgumentoException {
		Fragmento c = new Fragmento(novoResumo, grupo);
		c.valor = valor;
		return c;
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(FragmentoConstantes.FRAGMENTO);
		util.atributo("resumo", resumo);
		util.atributo("grupo", grupo);
		util.fecharTag();
		util.abrirTag2(Constantes.VALOR);
		util.conteudo("<![CDATA[").ql();
		util.tab().conteudo(getValor()).ql();
		util.conteudo("]]>").ql();
		util.finalizarTag(Constantes.VALOR);
		util.finalizarTag(FragmentoConstantes.FRAGMENTO);
	}

	public String getResumo() {
		return resumo;
	}

	public String getGrupo() {
		return grupo;
	}

	public boolean isValido() {
		return !Util.isEmpty(resumo) && !Util.isEmpty(grupo);
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		if (Util.isEmpty(valor)) {
			valor = Constantes.VAZIO;
		}
		return valor;
	}

	public Fragmento cloneOr() throws ArgumentoException {
		String string = getValor().toUpperCase().trim();
		if (string.startsWith("AND ")) {
			if (getValor().trim().startsWith("and ")) {
				string = "or " + getValor().trim().substring(4);
			} else {
				string = "OR " + getValor().trim().substring(4);
			}
		}
		Fragmento resp = new Fragmento(resumo, grupo);
		resp.setValor(string);
		return resp;
	}
}