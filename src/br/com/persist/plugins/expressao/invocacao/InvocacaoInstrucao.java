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
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class InvocacaoInstrucao extends Instrucao implements LinkBiblioteca {
	private boolean biblioLocal;
	private String nomeBiblio;
	private String nomeFuncao;

	public InvocacaoInstrucao(String nome) {
		super(nome);
	}

	@Override
	public Instrucao novo() {
		return new InvocacaoInstrucao(nome);
	}

	@Override
	public void setParametros(String string) {
		String[] array = string.split(ExpressaoConstantes.ESPACO);
		nomeBiblio = array[0];
		nomeFuncao = array[1];
		biblioLocal = InvocacaoContexto.THIS.equals(nomeBiblio);
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
		Funcao invocar = biblio.getFuncao(nomeFuncao);
		Funcao clone = invocar.clonar();
		if (!clone.isNativo()) {
			try {
				setParametros(clone, pilhaOperando);
				pilhaFuncao.push(clone);
			} catch (Exception ex) {
				throw new ExpressaoException(stringPilhaMetodo(clone, pilhaFuncao), ex);
			}
		} else {
			List<Object> lista = null;
			AtomicBoolean pushPilhaOperando = new AtomicBoolean();
			Object resp = invocarNativo(lista, clone, pilhaFuncao, pilhaOperando, pushPilhaOperando);
			if (pushPilhaOperando.get()) {
				pilhaOperando.push(resp);
			}
		}
	}

	private static void setParametros(Funcao funcao, PilhaOperando pilhaOperando) throws ExpressaoException {
		List<Integer> indices = indiceParametros(funcao);
		for (int i = indices.size() - 1; i >= 0; i--) {
			Object valor = pilhaOperando.pop();
			funcao.setValorParametro(indices.get(i), valor);
		}
	}

	private static List<Integer> indiceParametros(Funcao funcao) {
		List<Integer> resp = new ArrayList<>();
		for (int i = 0; i < funcao.getTotalParametro(); i++) {
			resp.add(i);
		}
		return resp;
	}

	private Object invocarNativo(List<Object> lista, Funcao funcao, PilhaFuncao pilhaMetodo,
			PilhaOperando pilhaOperando, AtomicBoolean pushPilhaOperando) throws ExpressaoException {
		Object resposta = null;
		Class<?> klass = null;
		try {
			klass = Class.forName(funcao.getBiblioNativa());
		} catch (Exception ex) {
			throw new ExpressaoException("erro.biblio_inexistente", funcao.getBiblioNativa());
		}
		List<Integer> params = indiceParametros(funcao);
		Class<?>[] tipoParametros = getTipoParametros(params);
		Object[] valorParametros = getValorParametros(pilhaOperando, params);
		if (lista != null) {
			tipoParametros = editarTipoParametros(tipoParametros, lista);
			valorParametros = editarValorParametros(valorParametros, lista);
		}
		try {
			Method method = klass.getDeclaredMethod(funcao.getNome(), tipoParametros);
			Class<?> returnType = method.getReturnType();
			String string = returnType.getCanonicalName();
			if (funcao.isTipoVoid()) {
				method.invoke(klass, valorParametros);
				pushPilhaOperando.set(false);
			} else {
				if ("void".equals(string) || "java.lang.Void".equals(string)) {
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

	private Class<?>[] getTipoParametros(List<Integer> params) {
		Class<?>[] tipoParametros = new Class<?>[params.size()];
		for (int i = 0; i < params.size(); i++) {
			tipoParametros[i] = Object.class;
		}
		return tipoParametros;
	}

	private Object[] getValorParametros(PilhaOperando pilhaOperando, List<Integer> params) throws ExpressaoException {
		Object[] valorParametros = new Object[params.size()];
		for (int i = params.size() - 1; i >= 0; i--) {
			Object valor = pilhaOperando.pop();
			valorParametros[i] = valor;
		}
		return valorParametros;
	}

	private Class<?>[] editarTipoParametros(Class<?>[] tipoParametros, List<Object> lista) {
		List<Class<?>> resposta = new ArrayList<>(Arrays.asList(tipoParametros));
		for (int i = 0; i < lista.size(); i++) {
			resposta.add(Object.class);
		}
		return resposta.toArray(new Class<?>[0]);
	}

	private Object[] editarValorParametros(Object[] valorParametros, List<Object> lista) {
		List<Object> resposta = new ArrayList<>(Arrays.asList(valorParametros));
		for (Object item : lista) {
			resposta.add(item);
		}
		return resposta.toArray(new Object[0]);
	}

	private static String stringPilhaMetodo(Funcao funcao, PilhaFuncao pilhaMetodo) throws ExpressaoException {
		StringBuilder sb = new StringBuilder(funcao.toString() + "\n");
		while (!pilhaMetodo.isEmpty()) {
			sb.append(pilhaMetodo.pop() + "\n");
		}
		return sb.toString();
	}

	public static void validar(Funcao funcao, boolean comRetorno, int totalParam) throws ExpressaoException {
		if (funcao == null) {
			throw new ExpressaoException("Funcao nula.", false);
		}
		if (comRetorno && funcao.isTipoVoid()) {
			throw new ExpressaoException("erro.funcao_sem_retorno", funcao.getNome(),
					funcao.getBiblioteca().getNomeAbsoluto());
		} else if (!comRetorno && !funcao.isTipoVoid()) {
			throw new ExpressaoException("erro.funcao_com_retorno", funcao.getNome(),
					funcao.getBiblioteca().getNomeAbsoluto());
		}
		if (funcao.getTotalParametro() != totalParam) {
			throw new ExpressaoException("erro.divergencia_qtd_decl_invocacao", funcao.getNome(),
					funcao.getTotalParametro(), totalParam, funcao.getBiblioteca().getNomeAbsoluto());
		}
	}
}