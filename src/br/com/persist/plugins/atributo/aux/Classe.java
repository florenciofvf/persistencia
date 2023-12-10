package br.com.persist.plugins.atributo.aux;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.StringPool;

public class Classe extends Container {
	private final List<Anotacao> anotacoes;
	private final List<Import> imports;
	private final String nome;

	public Classe(String nome) {
		anotacoes = new ArrayList<>();
		imports = new ArrayList<>();
		this.nome = nome;
	}

	@Override
	public void add(Container c) {
		if (c instanceof Import) {
			imports.add((Import) c);
			return;
		}
		if (c instanceof Anotacao) {
			anotacoes.add((Anotacao) c);
			return;
		}
		super.add(c);
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		if (!imports.isEmpty()) {
			for (Import imp : imports) {
				imp.gerar(0, pool);
			}
			pool.ql();
		}
		for (Anotacao ano : anotacoes) {
			ano.gerar(0, pool);
		}
		pool.append("public class " + nome + " {").ql();
		for (Container c : lista) {
			c.gerar(1, pool);
		}
		pool.append("}");
	}
}