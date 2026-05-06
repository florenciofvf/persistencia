package br.com.persist.plugins.expressao.invocacao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBiblioteca;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

/**
 * <pre>
 * defun main() {
 * 	procedimento();
 * 	return funcao();
 * }
 * </pre>
 **/
public class InvokeInstrucao extends Invoke implements LinkBiblioteca {
	private final boolean comRetorno;
	private boolean biblioLocal;
	private String nomeBiblio;
	private String nomeFuncao;

	public InvokeInstrucao(boolean comRetorno, int indice, String parametros) throws ExpressaoException {
		super(indice, comRetorno ? InvocacaoContexto.INVOKE_CRET : InvocacaoContexto.INVOKE_VOID);
		this.comRetorno = comRetorno;
		String[] array = parametros.split(ExpressaoConstantes.ESPACO);
		nomeBiblio = array[0];
		nomeFuncao = array[1];
		biblioLocal = Contexto.THIS.equals(nomeBiblio);
	}

	@Override
	public String getNomeBiblioAbsoluto() {
		return nomeBiblio;
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
		Funcao funcaoLoad = biblio.getFuncao(nomeFuncao).clonarSemParent();
		validar(funcaoLoad, comRetorno);
		pilhaOperando.setArgumentos(funcaoLoad);
		if (funcaoLoad.isNativo()) {
			List<Object> lista = null;
			if ("br.com.persist.plugins.expressao.biblionativo.Biblioteca".equals(nomeBiblio)) {
				lista = new ArrayList<>(Arrays.asList(funcao.getBiblioteca()));
			}
			AtomicBoolean pushPilhaOperando = new AtomicBoolean();
			Object resp = invocarNativo(lista, funcaoLoad, pilhaFuncao, pushPilhaOperando);
			if (pushPilhaOperando.get()) {
				pilhaOperando.push(resp);
			}
		} else {
			pilhaFuncao.push(funcaoLoad);
		}
	}

	private Object invocarNativo(List<Object> lista, Funcao funcao, PilhaFuncao pilhaMetodo,
			AtomicBoolean pushPilhaOperando) throws ExpressaoException {
		Object resposta = null;
		Class<?> klass = null;
		try {
			klass = Class.forName(funcao.getBiblioNativa());
		} catch (Exception ex) {
			throw new ExpressaoException("erro.biblio_inexistente", funcao.getBiblioNativa());
		}
		Class<?>[] tipoParametros = funcao.getTipoParametros();
		Object[] valorParametros = funcao.getValorParametros();
		if (lista != null) {
			tipoParametros = editarTipoParametros(tipoParametros, lista);
			valorParametros = editarValorParametros(valorParametros, lista);
		}
		try {
			Method method = klass.getDeclaredMethod(funcao.getNome(), tipoParametros);
			Class<?> returnType = method.getReturnType();
			String canonicalName = returnType.getCanonicalName();
			if (funcao.isTipoVoid()) {
				if (!isVoid(canonicalName)) {
					throw new ExpressaoException("erro.funcao_nativa_retorno_cret", funcao.getNome(),
							funcao.getBiblioteca().getNomeAbsoluto());
				}
				method.invoke(klass, valorParametros);
				pushPilhaOperando.set(false);
			} else {
				if (isVoid(canonicalName)) {
					throw new ExpressaoException("erro.funcao_nativa_retorno_void", funcao.getNome(),
							funcao.getBiblioteca().getNomeAbsoluto());
				}
				resposta = method.invoke(klass, valorParametros);
				pushPilhaOperando.set(true);
			}
		} catch (Exception ex) {
			throw new ExpressaoException(stringPilhaMetodo(funcao, pilhaMetodo), ex);
		}
		return resposta;
	}

	private static Class<?>[] editarTipoParametros(Class<?>[] tipoParametros, List<Object> lista) {
		List<Class<?>> resp = new ArrayList<>(Arrays.asList(tipoParametros));
		for (int i = 0; i < lista.size(); i++) {
			resp.add(Object.class);
		}
		return resp.toArray(new Class<?>[0]);
	}

	private static Object[] editarValorParametros(Object[] valorParametros, List<Object> lista) {
		List<Object> resp = new ArrayList<>(Arrays.asList(valorParametros));
		for (Object item : lista) {
			resp.add(item);
		}
		return resp.toArray(new Object[0]);
	}

	@Override
	public String toString() {
		return super.toString() + " " + nomeBiblio + "." + nomeFuncao;
	}
}