package br.com.persist.plugins.expressao.funcao;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Load;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

/**
 * <pre>
*defun teste(funcao) {
*	return defun inner(param) {
*		return defun outra() {
*			return funcao();
*		};
*	};
*}
 * </pre>
 */
public class FuncaoLoadInnerInstrucao extends FuncaoLoad implements Load {
	private final boolean tipoVoid;
	private String nomeBiblioteca;
	private String nomeFuncao;

	public FuncaoLoadInnerInstrucao(boolean tipoVoid, int indice, String parametros) throws ExpressaoException {
		super(indice, tipoVoid ? FuncaoContexto.LOAD_FUNCTION_INNER_VOID : FuncaoContexto.LOAD_FUNCTION_INNER_CRET);
		this.tipoVoid = tipoVoid;
		String[] array = parametros.split(ExpressaoConstantes.ESPACO);
		nomeBiblioteca = array[0];
		nomeFuncao = array[1];
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Biblioteca biblio = funcao.getBiblioteca();
		Funcao funcaoLoad = biblio.getFuncao(nomeFuncao).clonar();
		checarTipo(tipoVoid, funcaoLoad, nomeBiblioteca, nomeFuncao);
		funcaoLoad.setParent(funcao);
		pilhaOperando.push(funcaoLoad);
		log(getA(nomeBiblioteca, nomeFuncao) + "] [funcao_inner_carregada->" + funcaoLoad + "]", pilhaOperando);
	}

	private String getA(String nomeBiblioteca, String nomeFuncao) {
		return "[" + (tipoVoid ? FuncaoContexto.LOAD_FUNCTION_INNER_VOID : FuncaoContexto.LOAD_FUNCTION_INNER_CRET)
				+ get(nomeBiblioteca, nomeFuncao);
	}

	@Override
	public String toString() {
		return super.toString() + get(nomeBiblioteca, nomeFuncao);
	}
}