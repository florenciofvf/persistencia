package br.com.persist.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.xml.sax.Attributes;

import br.com.persist.componente.Menu;
import br.com.persist.fabrica.Fabrica;
import br.com.persist.fabrica.FabricaContainer;

public class MenuApp {
	private final List<MenuApp> filhos;
	private String classeFabrica;
	private String descricao;
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

	public Menu criarMenu() {
		Menu menu = new Menu(descricao, getIcon(), Constantes.VAZIO);

		for (MenuApp filho : filhos) {
			Menu item = filho.criarItem();

			if (item != null) {
				menu.add(menu);
			}
		}

		return menu;
	}

	public Menu criarItem() {
		FabricaContainer fabricaContainer = Fabrica.criar(classeFabrica + Constantes.SEP);

		if (fabricaContainer != null) {
			return fabricaContainer.criarMenu();
		}

		return null;
	}
}