package br.com.persist.plugins.propriedade;

import java.util.Map;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Propriedade extends Container {
	public Propriedade(String nome, String valor) {
		super(nome);
		setValor(valor);
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}

	@Override
	public void salvar(Container pai, XMLUtil util) {
		Map<String, String> mapString = ((Bloco) pai).getMapString();
		String string = getValor();

		for (Map.Entry<String, String> entry : mapString.entrySet()) {
			string = Util.replaceAll(string, Constantes.SEP + entry.getKey() + Constantes.SEP, entry.getValue());
		}

		util.abrirTag(PropriedadeHandler.PROPERTY);
		util.atributo(PropriedadeHandler.NAME, getNome());
		util.atributo(PropriedadeHandler.VALUE, string);
		util.fecharTag(-1);
	}
}