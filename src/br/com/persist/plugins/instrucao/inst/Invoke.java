package br.com.persist.plugins.instrucao.inst;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Invoke extends Instrucao {
	private String nomeMetodo;

	public Invoke(Metodo metodo) {
		super(metodo, InstrucaoConstantes.INVOKE);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		Invoke resp = new Invoke(metodo);
		resp.nomeMetodo = nomeMetodo;
		return resp;
	}

	@Override
	public void setParam(String string) throws InstrucaoException {
		if (string == null) {
			throw new InstrucaoException("Invoke metodo null.");
		}
		this.nomeMetodo = string;
	}

	@Override
	public String getParam() {
		return nomeMetodo;
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		String[] array = nomeMetodo.split("\\.");
		Biblioteca biblioteca;
		Metodo invocar;
		if (array.length == 2) {
			biblioteca = cacheBiblioteca.getBiblioteca(array[0]);
			invocar = biblioteca.getMetodo(array[1]);
		} else {
			if (metodo == null) {
				throw new InstrucaoException("erro.metodo_inexistente", "null", "null");
			}
			biblioteca = metodo.getBiblioteca();
			invocar = biblioteca.getMetodo(array[0]);
		}
		Metodo clone = invocar.clonar();
		if (!clone.isNativo()) {
			try {
				setParametros(clone, pilhaOperando);
				pilhaMetodo.push(clone);
			} catch (Exception ex) {
				throw new InstrucaoException(stringPilhaMetodo(clone, pilhaMetodo), ex);
			}
		} else {
			Object resp = invocarNativo(clone, pilhaMetodo, pilhaOperando);
			pilhaOperando.push(resp);
		}
	}

	static void setParametros(Metodo metodo, PilhaOperando pilhaOperando) throws InstrucaoException {
		List<Integer> params = listaParam(metodo);
		for (int i = params.size() - 1; i >= 0; i--) {
			Object valor = pilhaOperando.pop();
			metodo.setValorParam(params.get(i), valor);
		}
	}

	static List<Integer> listaParam(Metodo metodo) {
		List<Integer> resp = new ArrayList<>();
		for (int i = 0; i < metodo.getTotalParam(); i++) {
			resp.add(i);
		}
		return resp;
	}

	private Object invocarNativo(Metodo metodo, PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando)
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

	static String stringPilhaMetodo(Metodo metodo, PilhaMetodo pilhaMetodo) throws InstrucaoException {
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