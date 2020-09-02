package br.com.persist.principal;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.util.MenuApp;

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