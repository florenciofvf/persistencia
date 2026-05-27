package br.com.persist.plugins.expressao.constante;

import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Invoke;
import br.com.persist.plugins.expressao.processador.Mapa;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ConstanteInvokeMapaInstrucao extends Instrucao implements Invoke, Mapa {
	private final boolean comRetorno;
	private String nomeConstante;
	private String nomeMetodo;

	public ConstanteInvokeMapaInstrucao(boolean comRetorno, int indice, String parametros) throws ExpressaoException {
		super(indice, comRetorno ? ConstanteContexto.INVOKE_CONST_MAPA_CRET : ConstanteContexto.INVOKE_CONST_MAPA_VOID);
		this.comRetorno = comRetorno;
		String[] array = parametros.split(ExpressaoConstantes.ESPACO);
		nomeConstante = array[0];
		nomeMetodo = array[1];
	}

	@Override
	@SuppressWarnings("unchecked")
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Biblioteca biblio = funcao.getBiblioteca();
		Constante constante = biblio.getConstante(nomeConstante);
		Map<Object, Object> mapa = (Map<Object, Object>) constante.getValor();
		Funcao funcaoValor = (Funcao) mapa.get(nomeMetodo);
		Funcao clone = funcaoValor.clonar();
		pilhaOperando.setArgumentos(clone);
		pilhaFuncao.push(clone);
		log(get() + get(nomeConstante, nomeMetodo) + "] [funcao_valor->" + clone + "]", pilhaOperando);
	}

	private String get() {
		return "[" + (comRetorno ? ConstanteContexto.INVOKE_CONST_MAPA_CRET : ConstanteContexto.INVOKE_CONST_MAPA_VOID);
	}

	@Override
	public String toString() {
		return super.toString() + get(nomeConstante, nomeMetodo);
	}
}