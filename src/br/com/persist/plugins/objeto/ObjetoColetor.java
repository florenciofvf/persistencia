package br.com.persist.plugins.objeto;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.objeto.internal.InternalForm;

public class ObjetoColetor {
	private final AtomicBoolean ajusteLarguraForm;
	private final AtomicBoolean compararRegistros;
	private final AtomicBoolean ajusteAutoForm;
	private final List<InternalForm> forms;
	private final StringBuilder sbConexao;
	private final AtomicBoolean processar;
	private final List<Relacao> relacoes;
	private final List<Objeto> objetos;
	private final Dimension dimension;
	private String arquivoVinculo;

	public ObjetoColetor() {
		ajusteLarguraForm = new AtomicBoolean();
		compararRegistros = new AtomicBoolean();
		ajusteAutoForm = new AtomicBoolean();
		processar = new AtomicBoolean();
		sbConexao = new StringBuilder();
		relacoes = new ArrayList<>();
		dimension = new Dimension();
		objetos = new ArrayList<>();
		forms = new ArrayList<>();
	}

	public void init() {
		if (sbConexao.length() > 0) {
			sbConexao.delete(0, sbConexao.length());
		}
		ajusteLarguraForm.set(false);
		compararRegistros.set(false);
		ajusteAutoForm.set(false);
		dimension.setSize(0, 0);
		arquivoVinculo = null;
		processar.set(false);
		relacoes.clear();
		objetos.clear();
		forms.clear();
	}

	public void setArquivoVinculo(String arquivoVinculo) {
		this.arquivoVinculo = arquivoVinculo;
	}

	public AtomicBoolean getAjusteAutoForm() {
		return ajusteAutoForm;
	}

	public AtomicBoolean getAjusteLarguraForm() {
		return ajusteLarguraForm;
	}

	public AtomicBoolean getCompararRegistros() {
		return compararRegistros;
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

	public String getArquivoVinculo() {
		return arquivoVinculo;
	}

	public AtomicBoolean getProcessar() {
		return processar;
	}
}