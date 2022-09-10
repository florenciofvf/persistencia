package br.com.persist.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Constantes;

public class Objeto extends Tipo {
	private final List<NomeValor> atributos;
	private String tempNomeAtributo;

	public Objeto() {
		atributos = new ArrayList<>();
	}

	public List<NomeValor> getAtributos() {
		return atributos;
	}

	public Object converter(Object object)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		PoolMetodo poolMetodo = new PoolMetodo(object.getClass());
		for (NomeValor par : atributos) {
			if (par.isNull()) {
				continue;
			}
			Method metodoSet = poolMetodo.getMethodSet(par.nome);
			if (metodoSet != null) {
				if (par.compativel(metodoSet)) {
					par.invoke(object, metodoSet);
				} else if (par.isObjeto()) {
					Class<?> classe = metodoSet.getParameterTypes()[0];
					Object objeto = classe.newInstance();
					metodoSet.invoke(object, objeto);
					((Objeto) par.valor).converter(objeto);
				}
			}
		}
		return object;
	}

	public void addAtributo(String nome, Tipo tipo) {
		if (getAtributo(nome) == null) {
			tipo.pai = this;
			atributos.add(new NomeValor(nome, tipo));
		}
	}

	public Tipo getAtributo(String nome) {
		for (NomeValor par : atributos) {
			if (par.nome.equals(nome)) {
				return par.valor;
			}
		}
		return null;
	}

	public void preAtributo() throws DataException {
		if (atributos.isEmpty()) {
			throw new DataException("Objeto virgula");
		}
	}

	public void checkDoisPontos() throws DataException {
		if (tempNomeAtributo == null) {
			throw new DataException("Objeto tempNomeAtributo null");
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for (int i = 0; i < atributos.size() && i < 1; i++) {
			sb.append(atributos.get(i));
		}
		for (int i = 1; i < atributos.size(); i++) {
			sb.append("," + Constantes.QL);
			sb.append(atributos.get(i));
		}
		sb.append("}");
		return sb.toString();
	}

	class PoolMetodo {
		final Method[] metodos;

		PoolMetodo(Class<?> classe) {
			metodos = classe.getDeclaredMethods();
		}

		Method getMethodSet(String nome) {
			String nomeMetodo = "set" + nome.substring(0, 1).toUpperCase() + nome.substring(1);
			for (Method m : metodos) {
				if (nomeMetodo.equals(m.getName())) {
					Class<?>[] parameterTypes = m.getParameterTypes();
					if (parameterTypes.length == 1) {
						return m;
					}
				}
			}
			return null;
		}
	}

	public void processar(Tipo tipo) throws DataException {
		if (tempNomeAtributo != null) {
			addAtributo(tempNomeAtributo, tipo);
			tempNomeAtributo = null;
		} else if (tipo instanceof Texto) {
			tempNomeAtributo = ((Texto) tipo).getConteudo();
		} else {
			throw new DataException("Tipo invalido >>> " + tipo);
		}
	}
}