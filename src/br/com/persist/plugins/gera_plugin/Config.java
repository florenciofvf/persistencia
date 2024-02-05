package br.com.persist.plugins.gera_plugin;

import java.io.File;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

class Config {
	String nomeDecapitalizado;
	String nomeCapitalizado;
	String nomeCaixaBaixa;
	String nomeCaixaAlta;
	boolean configuracao;
	String nomeMin;
	String recurso;
	String pacote;
	String icone;
	File destino;
	String meta;

	String processar(String string) {
		string = Util.replaceAll(string, tag("decapLower"), nomeCaixaBaixa);
		string = Util.replaceAll(string, tag("decap"), nomeDecapitalizado);
		string = Util.replaceAll(string, tag("capUpper"), nomeCaixaAlta);
		string = Util.replaceAll(string, tag("cap"), nomeCapitalizado);
		string = Util.replaceAll(string, tag("nomeMin"), nomeMin);
		string = Util.replaceAll(string, tag("package"), pacote);
		string = Util.replaceAll(string, tag("icone"), icone);
		return string;
	}

	String tag(String string) {
		return Constantes.SEP + string + Constantes.SEP;
	}

	boolean comRecurso() {
		return !Util.isEmpty(recurso);
	}
}