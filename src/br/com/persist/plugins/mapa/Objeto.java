package br.com.persist.plugins.mapa;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.mapa.organiza.Organizador;

public class Objeto {
	private Vetor3D v = new Vetor3D(0, 0, 0);
	private final List<Atributo> atributos;
	private final List<Objeto> referencias;
	private final List<Objeto> filhos;
	private Organizador organizador;
	private final Set<Add> setAdds;
	private final Set<Ref> setRefs;
	protected final String nome;
	private Color corGradiente1;
	private Color corGradiente2;
	private int diametro;
	private Color corRGB;
	private String menu;
	boolean centro;
	Vetor3D vetor;
	int xOrigem;
	int yOrigem;

	public Objeto(String nome) throws ArgumentoException {
		if (Util.isEmpty(nome)) {
			throw new ArgumentoException("Nome do objeto vazio.");
		}
		referencias = new ArrayList<>();
		atributos = new ArrayList<>();
		filhos = new ArrayList<>();
		setAdds = new HashSet<>();
		setRefs = new HashSet<>();
		this.nome = nome;
	}

	public void preDesenhar(int x, int y, int z, int diametro) {
		corGradiente2 = corRGB != null ? corRGB : Color.BLACK;
		vetor = new Vetor3D(x, y, z);
		corGradiente1 = Color.WHITE;
		this.diametro = diametro;
	}

	public void desenhar(Graphics2D g2) {
		int diamet = this.diametro + (int) (vetor.z / 10);
		int diametMet = diamet / 2;

		GradientPaint gradiente = new GradientPaint((float) (xOrigem + vetor.x), (float) (yOrigem + vetor.y),
				corGradiente1, (float) (xOrigem + vetor.x + diamet), (float) (yOrigem + vetor.y + diamet),
				corGradiente2);

		g2.setPaint(gradiente);

		g2.fill(new Ellipse2D.Double(xOrigem + vetor.x, yOrigem + vetor.y, diamet, diamet));

		g2.setColor(Color.BLACK);

		g2.drawString(nome, (int) (xOrigem + vetor.x), (int) (yOrigem + vetor.y + diametMet));
		g2.drawString("(" + filhos.size() + ")", (int) (xOrigem + vetor.x + 10),
				(int) (yOrigem + vetor.y + diametMet + 10));

		if (Config.isDesenharAtributos()) {
			int i = atributos.size();
			if (i > 0) {
				int metade = diamet / 2;
				int xx = xOrigem + metade;
				int yy = yOrigem + metade;
				v.x = (metade + 10);
				v.y = 0;
				v.z = 0;
				v.rotacaoZ(-75);

				int g = 180 / i;
				for (Atributo a : atributos) {
					g2.drawString(a.toString(), (int) (xx + vetor.x + v.x), (int) (yy + vetor.y + v.y));
					v.rotacaoZ(g);
				}
			}
		}
	}

	public boolean contem(int x, int y) {
		int diamet = this.diametro + (int) (vetor.z / 10);
		return (x >= xOrigem + vetor.x && x <= xOrigem + vetor.x + diamet)
				&& (y >= yOrigem + vetor.y && y <= yOrigem + vetor.y + diamet);
	}

	public int[] getXYCentro() {
		int diamet = this.diametro + (int) (vetor.z / 10);
		int metade = diamet / 2;
		int[] xy = new int[2];
		xy[0] = (int) (xOrigem + vetor.x + metade);
		xy[1] = (int) (yOrigem + vetor.y + metade);
		return xy;
	}

	public Vetor3D getVetor() {
		return vetor;
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

	public void lerAtributos(Attributes attributes) throws ArgumentoException {
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

	public List<Objeto> getFilhos() {
		return filhos;
	}

	public void adicionar(Add add) {
		if (add != null && !add.getNome().equalsIgnoreCase(nome)) {
			setAdds.add(add);
		}
	}

	public void adicionar(Ref ref) {
		if (ref != null && !ref.getNome().equalsIgnoreCase(nome)) {
			setRefs.add(ref);
		}
	}

	public void adicionar(Objeto objeto, boolean ref) {
		if (objeto == null || objeto.equals(this)) {
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
		Iterator<Ref> itRef = setRefs.iterator();
		while (itRef.hasNext()) {
			Ref ref = itRef.next();
			Objeto obj = mapaHandler.getObjeto(ref.getNome());
			adicionar(obj, true);
		}
		Iterator<Add> itAdd = setAdds.iterator();
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

	public Color getCorGradiente1() {
		return corGradiente1;
	}

	public void setCorGradiente1(Color corGradiente1) {
		if (corGradiente1 != null) {
			this.corGradiente1 = corGradiente1;
		}
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
		List<Associacao> resp = new ArrayList<>();
		for (Objeto obj : referencias) {
			if (objetos.contains(obj)) {
				resp.add(new Associacao(this, obj));
			}
		}
		return resp;
	}
}