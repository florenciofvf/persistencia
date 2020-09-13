package br.com.persist.plugins.objeto;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.objeto.internal.InternalForm;

public class ObjetoColetor {
	private final AtomicBoolean ajusteAutoForm;
	private final List<InternalForm> forms;
	private final StringBuilder sbConexao;
	private final List<Relacao> relacoes;
	private final List<Objeto> objetos;
	private final Dimension dimension;

	public ObjetoColetor() {
		ajusteAutoForm = new AtomicBoolean();
		sbConexao = new StringBuilder();
		dimension = new Dimension();
		relacoes = new ArrayList<>();
		objetos = new ArrayList<>();
		forms = new ArrayList<>();
	}

	public void init() {
		if (sbConexao.length() > 0) {
			sbConexao.delete(0, sbConexao.length());
		}
		ajusteAutoForm.set(false);
		dimension.setSize(0, 0);
		relacoes.clear();
		objetos.clear();
		forms.clear();
	}

	public AtomicBoolean getAjusteAutoForm() {
		return ajusteAutoForm;
	}

	public List<InternalForm> getForms() {
		return forms;
	}

	public StringBuilder getSbConexao() {
		return sbConexao;
	}

	public List<Relacao> getRelacoes() {
		return relacoes;
	}

	public List<Objeto> getObjetos() {
		return objetos;
	}

	public Dimension getDimension() {
		return dimension;
	}
}