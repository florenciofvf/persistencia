package br.com.persist.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Objeto extends Tipo {
	private final List<NomeValor> atributos;
	static final MutableAttributeSet att2;
	static final MutableAttributeSet att;
	private String tempNomeAtributo;

	public Objeto() {
		atributos = new ArrayList<>();
	}

	public void export(Container c, int tab) {
		Formatador.formatar(this, c, tab);
	}

	@Override
	public void append(Container c, int tab) {
		Formatador.append(this, c, tab);
	}

	public Object converter(Object object) {
		Conversor.converter(this, object);
		return object;
	}

	public List<NomeValor> getAtributos() {
		return atributos;
	}

	public void addAtributo(String nome, Tipo tipo) {
		if (getValor(nome) == null) {
			tipo.pai = this;
			atributos.add(new NomeValor(nome, tipo));
		}
	}

	public Tipo getValor(String nome) {
		for (NomeValor nomeValor : atributos) {
			if (nomeValor.nome.equals(nome)) {
				return nomeValor.valor;
			}
		}
		return null;
	}

	public NomeValor getAtributo(String nome) {
		for (NomeValor item : atributos) {
			if (item.nome.equals(nome)) {
				return item;
			}
		}
		return null;
	}

	public void atualizar(String nome, String valor) {
		for (int i = 0; i < atributos.size(); i++) {
			NomeValor nomeValor = atributos.get(i);
			if (nomeValor.nome.equals(nome)) {
				atributos.set(i, new NomeValor(nome, new Texto(valor)));
			}
		}
	}

	public boolean contemAtributo(String nome) {
		for (NomeValor nomeValor : atributos) {
			if (nomeValor.nome.equalsIgnoreCase(nome)) {
				return true;
			}
		}
		return false;
	}

	public Map<String, String> getAtributosString() {
		Map<String, String> map = new LinkedHashMap<>();
		for (NomeValor nomeValor : atributos) {
			if (nomeValor.isTexto()) {
				map.put(nomeValor.nome, nomeValor.valor.toString());
			}
		}
		return map;
	}

	public void preAtributo() throws DataException {
		if (atributos.isEmpty()) {
			throw new DataException("Objeto virgula");
		}
	}

	public void checkDoisPontos() throws DataException {
		if (tempNomeAtributo == null) {
			throw new DataException("Objeto tempNomeAtributo null");
		}
	}

	@Override
	public String toString() {
		return DataUtil.toString(this);
	}

	public void processar(Tipo tipo) throws DataException {
		if (tempNomeAtributo != null) {
			addAtributo(tempNomeAtributo, tipo);
			tempNomeAtributo = null;
		} else if (tipo instanceof Texto) {
			tempNomeAtributo = ((Texto) tipo).getConteudo();
		} else {
			throw new DataException("Tipo invalido >>> " + tipo);
		}
	}

	@Override
	public boolean contem(String string) {
		return false;
	}

	@Override
	public Tipo clonar() {
		Objeto objeto = new Objeto();
		for (NomeValor nomeValor : atributos) {
			objeto.addAtributo(nomeValor.nome, nomeValor.valor.clonar());
		}
		return objeto;
	}

	static {
		att2 = new SimpleAttributeSet();
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, Color.BLACK);
		StyleConstants.setForeground(att2, new Color(125, 0, 0));
	}

	public void filtrarComAtributos(String[] atts, String valorAtributo) {
		Iterator<NomeValor> it = atributos.iterator();
		while (it.hasNext()) {
			NomeValor nv = it.next();
			Tipo tipo = nv.valor;
			if (tipo instanceof Objeto) {
				Objeto objeto = Filtro.comAtributos((Objeto) tipo, atts, valorAtributo);
				if (objeto == null) {
					it.remove();
				} else {
					objeto.filtrarComAtributos(atts, valorAtributo);
				}
			} else if (tipo instanceof Array) {
				((Array) tipo).filtrarComAtributos(atts, valorAtributo);
			}
		}
	}

	public void filtrarSemAtributos(String[] atts) {
		Iterator<NomeValor> it = atributos.iterator();
		while (it.hasNext()) {
			NomeValor nv = it.next();
			Tipo tipo = nv.valor;
			if (tipo instanceof Objeto) {
				Objeto objeto = Filtro.semAtributos((Objeto) tipo, atts);
				if (objeto == null) {
					it.remove();
				} else {
					objeto.filtrarSemAtributos(atts);
				}
			} else if (tipo instanceof Array) {
				((Array) tipo).filtrarSemAtributos(atts);
			}
		}
	}
}