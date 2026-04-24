package br.com.persist.plugins.expressao.constante;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBiblioteca;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.invocacao.InvocacaoInstrucao;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ConstanteInvokeInstrucao extends Instrucao implements LinkBiblioteca {
	private String nomeBiblioteca;
	private String nomeConstante;
	private boolean biblioLocal;

	public ConstanteInvokeInstrucao() {
		super(InvocacaoContexto.INVOKE_CONST);
	}

	@Override
	public Instrucao novo() {
		return new ConstanteInvokeInstrucao();
	}

	@Override
	public void setParametros(String string) {
		String[] array = string.split(ExpressaoConstantes.ESPACO);
		nomeBiblioteca = array[0];
		nomeConstante = array[1];
		biblioLocal = Contexto.THIS.equals(nomeBiblioteca);
	}

	@Override
	public String getNomeBiblioAbsoluto() {
		return nomeBiblioteca;
	}

	@Override
	public boolean isRefLocal() {
		return biblioLocal;
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Biblioteca biblio;
		if (biblioLocal) {
			biblio = funcao.getBiblioteca();
		} else {
			biblio = (Biblioteca) pilhaOperando.pop();
		}
		Constante constante = biblio.getConstante(nomeConstante);
		Funcao funcaoParam = (Funcao) constante.getValor();
		InvocacaoInstrucao.setArgumentos(funcaoParam, pilhaOperando);
		pilhaFuncao.push(funcaoParam);
	}
}