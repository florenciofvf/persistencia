package br.com.persist.plugins.gera_plugin;

import java.io.File;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

class Config {
	final File diretorioDestino;
	boolean configuracao;
	boolean comDialogo;
	String nameUpper;
	String nameLower;
	String nameDecap;
	String nameCap;
	String nameMin;
	String recurso;
	String pacote;
	String icone;

	public Config(File diretorioDestino) {
		this.diretorioDestino = Objects.requireNonNull(diretorioDestino);
	}

	String processar(String string) {
		string = Util.replaceAll(string, tag("nameUpper"), nameUpper);
		string = Util.replaceAll(string, tag("nameLower"), nameLower);
		string = Util.replaceAll(string, tag("nameDecap"), nameDecap);
		string = Util.replaceAll(string, tag("nameCap"), nameCap);
		string = Util.replaceAll(string, tag("nameMin"), nameMin);
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