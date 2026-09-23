package br.com.persist.formulario;

import org.xml.sax.Attributes;

import br.com.persist.abstrato.FabricaContainer;
import br.com.persist.assistencia.Fabrica;
import br.com.persist.componente.Menu;

public class MenuItemXML {
	private final String classeFabrica;
	private final boolean separador;
	private final boolean ativo;

	public MenuItemXML(String classeFabrica, boolean separador, boolean ativo) {
		this.classeFabrica = classeFabrica;
		this.separador = separador;
		this.ativo = ativo;
	}

	public boolean isSeparador() {
		return separador;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public static MenuItemXML criar(Attributes attr) {
		boolean separador = Boolean.parseBoolean(attr.getValue("separador"));
		boolean ativo = Boolean.parseBoolean(attr.getValue("ativo"));
		String classeFabrica = attr.getValue("classeFabrica");
		return new MenuItemXML(classeFabrica, separador, ativo);
	}

	public void processar(Formulario formulario, Menu menu) {
		FabricaContainer fabricaContainer = Fabrica.criar(classeFabrica);
		if (fabricaContainer != null) {
			fabricaContainer.inicializar();
			formulario.adicionarServicos(fabricaContainer.getServicos(formulario));
			formulario.adicionarFabrica(classeFabrica, fabricaContainer);
			fabricaContainer.processarMenu(formulario, menu);
		}
	}
}