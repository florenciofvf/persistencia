package br.com.persist.plugins.gera_plugin;

import java.io.File;

import br.com.persist.assistencia.Util;

class Config {
	String nomeDecapLower;
	boolean configuracao;
	String nomeCapUpper;
	String nomeDecap;
	String nomeMin;
	String nomeCap;
	String recurso;
	String pacote;
	String icone;
	File destino;

	String processar(String string) {
		string = Util.replaceAll(string, tag("decapLower"), nomeDecapLower);
		string = Util.replaceAll(string, tag("capUpper"), nomeCapUpper);
		string = Util.replaceAll(string, tag("nomeMin"), nomeMin);
		string = Util.replaceAll(string, tag("decap"), nomeDecap);
		string = Util.replaceAll(string, tag("package"), pacote);
		string = Util.replaceAll(string, tag("cap"), nomeCap);
		string = Util.replaceAll(string, tag("icone"), icone);
		return string;
	}

	String tag(String string) {
		return "###" + string + "###";
	}

	boolean comRecurso() {
		return !Util.estaVazio(recurso);
	}
}