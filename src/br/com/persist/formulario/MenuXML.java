package br.com.persist.formulario;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Menu;

public class MenuXML {
	private final List<MenuItemXML> itens;
	private final String descricao;
	private final boolean ativo;
	private final String icone;

	public MenuXML(String descricao, boolean ativo, String icone) {
		this.itens = new ArrayList<>();
		this.descricao = descricao;
		this.ativo = ativo;
		this.icone = icone;
	}

	public List<MenuItemXML> getItens() {
		return itens;
	}

	public void add(MenuItemXML item) {
		if (item != null) {
			itens.add(item);
		}
	}

	public boolean isAtivo() {
		return ativo;
	}

	public static MenuXML criar(Attributes attr) {
		boolean ativo = Boolean.parseBoolean(attr.getValue("ativo"));
		String descricao = attr.getValue("descricao");
		String icone = attr.getValue("icone");
		return new MenuXML(descricao, ativo, icone);
	}

	public Menu criarMenu() {
		return new Menu(descricao, false, getIcon());
	}

	public Icon getIcon() {
		if (Util.isEmpty(icone)) {
			return null;
		}
		try {
			return Imagens.getIcon(icone);
		} catch (AssistenciaException ex) {
			return null;
		}
	}
}