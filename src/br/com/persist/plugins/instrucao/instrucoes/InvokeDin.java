package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class InvokeDin extends Instrucao {
	private final Invoke invoke = new Invoke(null);
	private String nomeParam;

	public InvokeDin(Metodo metodo) {
		super(metodo, InstrucaoConstantes.INVOKE_DIN);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		InvokeDin resp = new InvokeDin(metodo);
		resp.nomeParam = nomeParam;
		return resp;
	}

	@Override
	public void setParam(String string) throws InstrucaoException {
		if (string == null) {
			throw new InstrucaoException("InvokeDin nomeParam null.");
		}
		this.nomeParam = string;
	}

	@Override
	public String getParam() {
		return nomeParam;
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object valor = metodo.getValorParam(nomeParam);
		Biblioteca biblioteca = metodo.getBiblioteca();
		invoke.setParam(biblioteca.getNome() + "." + valor.toString());
		invoke.executar(pilhaMetodo, pilhaOperando, cacheBiblioteca);
	}
}