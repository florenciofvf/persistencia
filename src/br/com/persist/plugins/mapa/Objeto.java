package br.com.persist.plugins.mapa;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.mapa.organiza.Organizador;

public class Objeto {
	private final List<Atributo> atributos;
	private final List<Objeto> referencias;
	private final Set<Ref> setReferencias;
	private final List<Objeto> filhos;
	private final Set<Add> setFilhos;
	private Organizador organizador;
	protected final String nome;
	private Color corRGB;
	private String menu;

	public Objeto(String nome) {
		if (Util.estaVazio(nome)) {
			throw new IllegalArgumentException("Nome do objeto vazio.");
		}
		setReferencias = new HashSet<>();
		referencias = new ArrayList<>();
		atributos = new ArrayList<>();
		setFilhos = new HashSet<>();
		filhos = new ArrayList<>();
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public void lerAtributos(Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			Atributo atributo = new Atributo(attributes.getQName(i), attributes.getValue(i));
			atributos.add(atributo);
		}
		criarCorRGB();
		checarMenu();
	}

	public List<Atributo> getAtributos() {
		return atributos;
	}

	public int getQtdReferencias() {
		return referencias.size();
	}

	public int getQtdFilhos() {
		return filhos.size();
	}

	public int getQtdAtributos() {
		return atributos.size();
	}

	public List<Objeto> getFilhos() {
		return filhos;
	}

	public void adicionar(Add add) {
		if (add != null) {
			setFilhos.add(add);
		}
	}

	public void adicionar(Ref ref) {
		if (ref != null) {
			setReferencias.add(ref);
		}
	}

	public void adicionar(Objeto objeto, boolean ref) {
		if (objeto == null) {
			return;
		}
		if (ref && !referencias.contains(objeto)) {
			referencias.add(objeto);
		} else if (!ref && !filhos.contains(objeto)) {
			filhos.add(objeto);
		}
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

	public void checarMenu() {
		Atributo attMenu = getAtributo("menu");
		if (attMenu != null) {
			menu = attMenu.getValor();
		}
	}

	public Atributo getAtributo(String nome) {
		for (Atributo a : atributos) {
			if (a.getNome().equalsIgnoreCase(nome)) {
				return a;
			}
		}
		return null;
	}

	public void resolverReferencias(MapaHandler mapaHandler) {
		Iterator<Ref> itRef = setReferencias.iterator();
		while (itRef.hasNext()) {
			Ref ref = itRef.next();
			Objeto obj = mapaHandler.getObjeto(ref.getNome());
			adicionar(obj, true);
		}
		Iterator<Add> itAdd = setFilhos.iterator();
		while (itAdd.hasNext()) {
			Add add = itAdd.next();
			Objeto obj = mapaHandler.getObjeto(add.getNome());
			adicionar(obj, false);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Objeto other = (Objeto) obj;
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return nome;
	}

	public Organizador getOrganizador() {
		return organizador;
	}

	public void setOrganizador(Organizador organizador) {
		this.organizador = organizador;
	}

	public List<Associacao> criarAssociacoes(List<Objeto> objetos) {
		return new ArrayList<>();
	}
}