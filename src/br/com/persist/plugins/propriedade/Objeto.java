package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class Objeto extends Container {
	private List<Campo> cacheCampos;
	private final String id;

	public Objeto(String id) {
		this.id = Objects.requireNonNull(id);
	}

	public String getId() {
		return id;
	}

	@Override
	public void adicionar(Container c) {
		if (c instanceof Campo) {
			super.adicionar(c);
		} else {
			throw new IllegalStateException();
		}
	}

	List<Campo> getCacheCampos() {
		if (cacheCampos != null) {
			return cacheCampos;
		}
		cacheCampos = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Campo) {
				cacheCampos.add((Campo) c);
			}
		}
		return cacheCampos;
	}

	public String substituir(String chave, String string) {
		for (Campo c : getCacheCampos()) {
			string = Util.replaceAll(string, Constantes.SEP + chave + "." + c.getNome() + Constantes.SEP, c.getValor());
		}
		return string;
	}

	@Override
	public String toString() {
		return "Objeto [id=" + id + "]";
	}
}