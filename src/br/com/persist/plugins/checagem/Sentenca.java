package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Sentenca {
	protected final List<Sentenca> parametros;
	protected Sentenca pai;

	public Sentenca() {
		parametros = new ArrayList<>();
	}

	public abstract Object executar(Contexto ctx);

	public Sentenca param0() {
		return parametros.get(0);
	}

	public Sentenca param1() {
		return parametros.get(1);
	}

	public void addParam(Sentenca sentenca) {
		if (sentenca == this) {
			throw new IllegalStateException("addParam sentenca == this");
		}
		sentenca.pai = this;
		parametros.add(sentenca);
	}

	public Sentenca getPai() {
		return pai;
	}

	public void checarProximo(Token atual, Token proximo, AtomicReference<Sentenca> sel) throws ChecagemException {
		if (proximo == null) {
			Sentenca selecionado = sel.get();
			selecionado.addParam(this);
			return;
		}
		if (proximo.isParenteseAbrir()) {
			String classe = ChecagemGramatica.map.get(atual.getValor().toLowerCase());
			Sentenca sentenca = criarSentenca(classe);
			Sentenca selecionado = sel.get();
			selecionado.addParam(sentenca);
			sel.set(sentenca);
			return;
		}
		if (!proximo.isVirgula() && !proximo.isParenteseFechar()) {
			throw new ChecagemException("proximo invalido >>> " + proximo.getValor());
		}
		if (proximo.isVirgula()) {
			Sentenca selecionado = sel.get();
			selecionado.addParam(this);
			return;
		}
		if (proximo.isParenteseFechar()) {
			Sentenca selecionado = sel.get();
			selecionado.addParam(this);
			sel.set(selecionado.pai);
		}
	}

	private Sentenca criarSentenca(String classe) throws ChecagemException {
		try {
			return (Sentenca) Class.forName(classe).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new ChecagemException("Classe inexistente >>> " + classe);
		}
	}
}