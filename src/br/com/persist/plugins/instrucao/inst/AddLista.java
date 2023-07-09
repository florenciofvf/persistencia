package br.com.persist.plugins.instrucao.inst;

import br.com.persist.assistencia.Lista;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class AddLista extends Matemat {
	public AddLista(Metodo metodo) {
		super(metodo, InstrucaoConstantes.ADD_LISTA);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new AddLista(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object operandoD = pilhaOperando.pop();
		Object operandoE = pilhaOperando.pop();
		InstrucaoUtil.checarOperando(operandoE);
		InstrucaoUtil.checarOperando(operandoD);
		Lista lista = new Lista();
		add(lista, operandoE);
		add(lista, operandoD);
		pilhaOperando.push(lista);
	}

	private void add(Lista lista, Object obj) {
		if (obj instanceof Lista) {
			lista.addLista((Lista) obj);
		} else {
			lista.add(obj);
		}
	}
}