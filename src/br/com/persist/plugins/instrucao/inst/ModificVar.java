package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class ModificVar extends Instrucao {
	private String param;

	public ModificVar(Metodo metodo) {
		super(metodo, InstrucaoConstantes.MODIFIC_VAR);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		ModificVar resp = new ModificVar(metodo);
		resp.param = param;
		return resp;
	}

	@Override
	public void setParam(String string) throws InstrucaoException {
		if (string == null) {
			throw new InstrucaoException("Var nome null.");
		}
		this.param = string;
	}

	@Override
	public String getParam() {
		return param;
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		String[] array = param.split("\\.");
		Object valor = pilhaOperando.pop();
		Biblioteca biblioteca;
		String nome;
		if (array.length == 2) {
			biblioteca = cacheBiblioteca.getBiblioteca(array[0]);
			nome = array[1];
		} else {
			biblioteca = metodo.getBiblioteca();
			nome = array[0];
		}
		biblioteca.setValorVariavel(nome, valor);
	}
}