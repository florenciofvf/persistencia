package br.com.persist.plugins.propriedade;

import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

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
	public void processar(Container pai, StyledDocument doc) throws BadLocationException {
		Map<String, String> mapString = ((Bloco) pai).getMapString();
		String string = getValor();

		for (Map.Entry<String, String> entry : mapString.entrySet()) {
			string = Util.replaceAll(string, Constantes.SEP + entry.getKey() + Constantes.SEP, entry.getValue());
		}

		PropriedadeUtil.iniPropriedade(PropriedadeHandler.PROPERTY, doc);
		PropriedadeUtil.atributo(PropriedadeHandler.NAME, doc);
		PropriedadeUtil.valorAtr(getNome(), doc);
		PropriedadeUtil.atributo(PropriedadeHandler.VALUE, doc);
		PropriedadeUtil.valorAtr(string, doc);
		PropriedadeUtil.fimPropriedade(doc);
	}
}