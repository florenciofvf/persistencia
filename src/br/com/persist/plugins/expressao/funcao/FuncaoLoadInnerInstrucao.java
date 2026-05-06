package br.com.persist.plugins.expressao.funcao;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.processador.Funcao;
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
public class FuncaoLoadInnerInstrucao extends FuncaoLoad {
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
		Funcao funcaoLoad = biblio.getFuncao(nomeFuncao).clonarSemParent();
		checarTipo(tipoVoid, funcaoLoad, nomeBiblioteca, nomeFuncao);
		funcaoLoad.setParent(funcao);
		funcaoLoad.checarHierarquia();
		pilhaOperando.push(funcaoLoad);
	}

	@Override
	public String toString() {
		return super.toString() + " " + nomeBiblioteca + "." + nomeFuncao;
	}
}