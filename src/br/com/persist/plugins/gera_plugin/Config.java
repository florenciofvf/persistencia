package br.com.persist.plugins.gera_plugin;

import java.io.File;

import br.com.persist.assistencia.Util;

class Config {
	String nomeDecapLower;
	String nomeCapUpper;
	String nomeDecap;
	String nomeMin;
	String nomeCap;
	String recurso;
	String pacote;
	File destino;

	String processar(String string) {
		string = Util.replaceAll(string, tag("decapLower"), nomeDecapLower);
		string = Util.replaceAll(string, tag("capUpper"), nomeCapUpper);
		string = Util.replaceAll(string, tag("decap"), nomeDecap);
		string = Util.replaceAll(string, tag("package"), pacote);
		string = Util.replaceAll(string, tag("cap"), nomeCap);
		return string;
	}

	String tag(String string) {
		return "###" + string + "###";
	}
}