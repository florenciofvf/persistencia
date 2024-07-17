package br.com.persist.plugins.instrucao.processador;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.InvocacaoContexto;

public class InvocacaoInstrucao extends Instrucao {
	private String nomeBiblio;
	private String nomeFuncao;

	public InvocacaoInstrucao() {
		super(InvocacaoContexto.INVOKE);
	}

	@Override
	public Instrucao clonar() {
		return this;
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
	public void processar(CacheBiblioteca cacheBiblioteca, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws InstrucaoException {
		Biblioteca biblioteca;
		Funcao invocar;
		if (nomeBiblio != null) {
			biblioteca = cacheBiblioteca.getBiblioteca(nomeBiblio);
		} else {
			if (funcao == null) {
				throw new InstrucaoException("erro.metodo_inexistente", "null", "null");
			}
			biblioteca = funcao.getBiblioteca();
		}
		invocar = biblioteca.getFuncao(nomeFuncao);
		Funcao clone = invocar.clonar();
		if (!clone.isNativo()) {
			try {
				setParametros(clone, pilhaOperando);
				pilhaFuncao.push(clone);
			} catch (Exception ex) {
				throw new InstrucaoException(stringPilhaMetodo(clone, pilhaFuncao), ex);
			}
		} else {
			Object resp = invocarNativo(clone, pilhaFuncao, pilhaOperando);
			pilhaOperando.push(resp);
		}
	}

	static void setParametros(Funcao metodo, PilhaOperando pilhaOperando) throws InstrucaoException {
		List<Integer> params = listaParam(metodo);
		for (int i = params.size() - 1; i >= 0; i--) {
			Object valor = pilhaOperando.pop();
			metodo.setValorParam(params.get(i), valor);
		}
	}

	static List<Integer> listaParam(Funcao metodo) {
		List<Integer> resp = new ArrayList<>();
		for (int i = 0; i < metodo.getTotalParam(); i++) {
			resp.add(i);
		}
		return resp;
	}

	private Object invocarNativo(Funcao metodo, PilhaFuncao pilhaMetodo, PilhaOperando pilhaOperando)
			throws InstrucaoException {
		Class<?> klass = null;
		try {
			klass = Class.forName(metodo.getBiblioNativa());
		} catch (Exception ex) {
			throw new InstrucaoException("erro.biblio_inexistente", metodo.getBiblioNativa());
		}
		List<Integer> params = listaParam(metodo);
		Class<?>[] tipoParametros = getTipoParametros(params);
		Object[] valorParametros = getValorParametros(pilhaOperando, params);
		try {
			Method method = klass.getDeclaredMethod(metodo.getNome(), tipoParametros);
			return method.invoke(klass, valorParametros);
		} catch (Exception ex) {
			throw new InstrucaoException(stringPilhaMetodo(metodo, pilhaMetodo), ex);
		}
	}

	static String stringPilhaMetodo(Funcao metodo, PilhaFuncao pilhaMetodo) throws InstrucaoException {
		StringBuilder sb = new StringBuilder(metodo.toString() + "\n");
		while (!pilhaMetodo.isEmpty()) {
			sb.append(pilhaMetodo.pop() + "\n");
		}
		return sb.toString();
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
}