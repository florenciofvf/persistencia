package br.com.persist.plugins.gera_plugin;

import java.io.File;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

class Config {
	final File diretorioDestino;
	boolean comConfiguracao;
	boolean comFichario;
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
		string = Util.replaceAll(string, tag("recurso"), recurso);
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

	String nameLowerEntre(String prefixo, String sufixo) {
		return prefixo + nameLower + sufixo;
	}

	String nameUpperEntre(String prefixo, String sufixo) {
		return prefixo + nameUpper + sufixo;
	}

	String nameLowerApos(String string) {
		return string + nameLower;
	}

	String nameUpperApos(String string) {
		return string + nameUpper;
	}

	String nameCapPaginaServico() {
		return nameCap + "PaginaServico";
	}

	String nameCapConfiguracao() {
		return nameCap + "Configuracao";
	}

	String nameCapPreferencia() {
		return nameCap + "Preferencia";
	}

	String nameCapConstantes() {
		return nameCap + "Constantes";
	}

	String nameCapFormulario() {
		return nameCap + "Formulario";
	}

	String nameCapContainer() {
		return nameCap + "Container";
	}

	String nameCapMensagens() {
		return nameCap + "Mensagens";
	}

	String nameCapFichario() {
		return nameCap + "Fichario";
	}

	String nameCapProvedor() {
		return nameCap + "Provedor";
	}

	String nameCapModelo() {
		return nameCap + "Modelo";
	}

	String nameCapDialogo() {
		return nameCap + "Dialogo";
	}

	String nameCapFabrica() {
		return nameCap + "Fabrica";
	}

	String nameCapServico() {
		return nameCap + "Servico";
	}

	String nameCapPagina() {
		return nameCap + "Pagina";
	}

	String nameCapSplit() {
		return nameCap + "Split";
	}

	String nameDecapFormulario() {
		return nameDecap + "Formulario";
	}

	String nameDecapDialogo() {
		return nameDecap + "Dialogo";
	}

	String declaracao() {
		return nameCap + " " + nameDecap;
	}
}