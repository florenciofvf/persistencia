package br.com.persist.plugins.instrucao.processador;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class Invocacao extends Instrucao {
	private String nomeBiblio;
	private String nomeFuncao;

	protected Invocacao(String nome) {
		super(nome);
	}

	@Override
	public void setParametros(String string) {
		String[] array = string.split("\\.");
		if (array.length == 2) {
			nomeBiblio = array[0];
			nomeFuncao = array[1];
		} else {
			nomeFuncao = array[0];
		}
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Biblioteca biblio;
		Funcao invocar;
		if (nomeBiblio != null) {
			biblio = cacheBiblioteca.getBiblioteca(nomeBiblio);
		} else {
			if (funcao == null) {
				throw new InstrucaoException("erro.funcao_inexistente", "null", "null");
			}
			if ("tailcall".equals(nomeFuncao)) {
				setParametros(funcao, pilhaOperando);
				funcao.setIndice(0);
				return;
			}
			biblio = funcao.getBiblioteca();
		}
		invocar = biblio.getFuncao(nomeFuncao);
		Funcao clone = invocar.clonar();
		if (!clone.isNativo()) {
			try {
				setParametros(clone, pilhaOperando);
				pilhaFuncao.push(clone);
			} catch (Exception ex) {
				throw new InstrucaoException(stringPilhaMetodo(clone, pilhaFuncao), ex);
			}
		} else {
			AtomicBoolean atomic = new AtomicBoolean();
			Object resp = invocarNativo(clone, pilhaFuncao, pilhaOperando, atomic);
			if (atomic.get()) {
				pilhaOperando.push(resp);
			}
		}
	}

	public static void validar(Funcao funcao, boolean exp, int totalParam) throws InstrucaoException {
		if (funcao == null) {
			throw new InstrucaoException("Funcao nula.", false);
		}
		if (exp && funcao.isTipoVoid()) {
			throw new InstrucaoException("erro.funcao_sem_retorno", funcao.getNome(), funcao.getBiblioteca().getNome());
		} else if (!exp && !funcao.isTipoVoid()) {
			throw new InstrucaoException("erro.funcao_com_retorno", funcao.getNome(), funcao.getBiblioteca().getNome());
		}
		if (funcao.getTotalParametro() != totalParam) {
			throw new InstrucaoException("erro.divergencia_qtd_decl_invocacao", funcao.getNome(),
					"" + funcao.getTotalParametro(), "" + totalParam);
		}
	}

	static void setParametros(Funcao funcao, PilhaOperando pilhaOperando) throws InstrucaoException {
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

	private Object invocarNativo(Funcao funcao, PilhaFuncao pilhaMetodo, PilhaOperando pilhaOperando,
			AtomicBoolean atomic) throws InstrucaoException {
		Object resposta = null;
		Class<?> klass = null;
		try {
			klass = Class.forName(funcao.getBiblioNativa());
		} catch (Exception ex) {
			throw new InstrucaoException("erro.biblio_inexistente", funcao.getBiblioNativa());
		}
		List<Integer> params = indiceParametros(funcao);
		Class<?>[] tipoParametros = getTipoParametros(params);
		Object[] valorParametros = getValorParametros(pilhaOperando, params);
		try {
			Method method = klass.getDeclaredMethod(funcao.getNome(), tipoParametros);
			Class<?> returnType = method.getReturnType();
			String string = returnType.getCanonicalName();
			if (funcao.isTipoVoid()) {
				method.invoke(klass, valorParametros);
				atomic.set(false);
			} else {
				if ("void".equals(string) || "java.lang.Void".equals(string)) {
					throw new InstrucaoException("erro.funcao_nativa_retorno_void", funcao.getNome(),
							funcao.getBiblioteca().getNome());
				}
				resposta = method.invoke(klass, valorParametros);
				atomic.set(true);
			}
		} catch (Exception ex) {
			throw new InstrucaoException(stringPilhaMetodo(funcao, pilhaMetodo), ex);
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

	private Object[] getValorParametros(PilhaOperando pilhaOperando, List<Integer> params) throws InstrucaoException {
		Object[] valorParametros = new Object[params.size()];
		for (int i = params.size() - 1; i >= 0; i--) {
			Object valor = pilhaOperando.pop();
			valorParametros[i] = valor;
		}
		return valorParametros;
	}

	private static String stringPilhaMetodo(Funcao funcao, PilhaFuncao pilhaMetodo) throws InstrucaoException {
		StringBuilder sb = new StringBuilder(funcao.toString() + "\n");
		while (!pilhaMetodo.isEmpty()) {
			sb.append(pilhaMetodo.pop() + "\n");
		}
		return sb.toString();
	}
}