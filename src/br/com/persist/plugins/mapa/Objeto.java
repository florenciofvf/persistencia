package br.com.persist.plugins.mapa;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class Objeto extends Container {
	private Set<String> set = new HashSet<>();
	private Color corRGB;
	private String menu;

	public Objeto(String nome) {
		super(nome);
	}

	public String getMenu() {
		return menu;
	}

	@Override
	public void adicionarAtributo(Atributo atributo) {
		super.adicionarAtributo(atributo);
		if ("menu".equalsIgnoreCase(atributo.getNome())) {
			menu = atributo.getValor();
		}
	}

	@Override
	public void adicionarFilho(Container container) {
		if (!container.isRef() && set.contains(container.nome)) {
			throw new IllegalStateException("Nome de objeto repetido [" + container.nome + "].");
		}
		set.add(container.nome);
		super.adicionarFilho(container);
	}

	public Color getCorRGB() {
		return corRGB;
	}

	public void setCorRGB(Color corRGB) {
		this.corRGB = corRGB;
	}

	public void criarCorRGB() {
		Atributo r = getAtributo("r");
		Atributo g = getAtributo("g");
		Atributo b = getAtributo("b");
		if (r != null && g != null && b != null) {
			corRGB = new Color(Integer.parseInt(r.getValor()), Integer.parseInt(g.getValor()),
					Integer.parseInt(b.getValor()));
		}
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}