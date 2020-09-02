package br.com.persist.principal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.xml.sax.Attributes;

import br.com.persist.componente.Menu;
import br.com.persist.fabrica.Fabrica;
import br.com.persist.fabrica.FabricaContainer;
import br.com.persist.util.Constantes;
import br.com.persist.util.Imagens;
import br.com.persist.util.Util;

public class MenuApp {
	private final List<MenuApp> filhos;
	private String classeFabrica;
	private String descricao;
	private boolean ativo;
	private String icone;
	private MenuApp pai;

	public MenuApp() {
		filhos = new ArrayList<>();
	}

	public String getClasseFabrica() {
		return classeFabrica;
	}

	public void add(MenuApp menu) {
		filhos.add(menu);
		menu.pai = this;
	}

	public void setClasseFabrica(String classeFabrica) {
		this.classeFabrica = classeFabrica;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public List<MenuApp> getFilhos() {
		return filhos;
	}

	public MenuApp getPai() {
		return pai;
	}

	public String getDescricao() {
		return descricao;
	}

	public void aplicar(Attributes attr) {
		ativo = Boolean.parseBoolean(attr.getValue("ativo"));
		classeFabrica = attr.getValue("classeFabrica");
		descricao = attr.getValue("descricao");
		icone = attr.getValue("icone");
	}

	public Icon getIcon() {
		if (Util.estaVazio(icone)) {
			return null;
		}

		return Imagens.getIcon(icone);
	}

	public Menu criarMenu(Formulario formulario) {
		Menu menu = new Menu(descricao, getIcon(), Constantes.VAZIO);

		for (MenuApp filho : filhos) {
			if (!filho.ativo) {
				continue;
			}

			List<Menu> itens = filho.criarItens(formulario);

			for (Menu item : itens) {
				menu.add(item);
			}
		}

		return menu;
	}

	public List<Menu> criarItens(Formulario formulario) {
		FabricaContainer fabricaContainer = Fabrica.criar(classeFabrica);
		List<Menu> menus = new ArrayList<>();

		if (fabricaContainer != null) {
			formulario.adicionarServicos(fabricaContainer.getServicos(formulario));
			formulario.adicionarFabrica(classeFabrica, fabricaContainer);
			menus.addAll(fabricaContainer.criarMenus(formulario));
		}

		return menus;
	}
}