package br.com.persist.formulario;

import java.util.ArrayList;
import java.util.List;

public class MenuColetor {
	private final List<MenuApp> menus;

	public MenuColetor() {
		menus = new ArrayList<>();
	}

	public void init() {
		menus.clear();
	}

	public List<MenuApp> getMenus() {
		return menus;
	}
}