package br.com.persist.plugins.checagem.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.checagem.ChecagemException;

public interface Arquivo {
	default List<String> get(Object obj) {
		List<String> resp = new ArrayList<>();
		if (obj instanceof List<?>) {
			List<?> lista = (List<?>) obj;
			for (Object o : lista) {
				resp.add(o.toString());
			}
		}
		return resp;
	}

	default List<Linha> get2(Object obj) {
		List<Linha> resp = new ArrayList<>();
		if (obj instanceof List<?>) {
			List<?> lista = (List<?>) obj;
			for (Object o : lista) {
				resp.add((Linha) o);
			}
		}
		return resp;
	}

	default void checar(Object obj) throws ChecagemException {
		if (!(obj instanceof List<?>)) {
			throw new ChecagemException(getClass(), " >>> op0 deve ser List<String>");
		}
	}

	default void checar2(Object obj) throws ChecagemException {
		if (!(obj instanceof List<?>)) {
			throw new ChecagemException(getClass(), " >>> op0 deve ser List<Linha>");
		}
	}

	default void checar(Long numero) throws ChecagemException {
		if (numero < 1) {
			throw new ChecagemException(getClass(), " >>> numero menor que 1");
		}
	}

	default void checar(Long numero, List<String> arquivo) throws ChecagemException {
		if (numero > arquivo.size()) {
			throw new ChecagemException(getClass(), " >>> numero maior que arquivo");
		}
	}

	default void checar2(Long numero, List<Linha> arquivo) throws ChecagemException {
		if (numero > arquivo.size()) {
			throw new ChecagemException(getClass(), " >>> numero maior que arquivo");
		}
	}

	default Linha criarLinha(Long numero, List<String> arquivo) {
		if (numero < 1 || numero > arquivo.size()) {
			return null;
		}
		String string = arquivo.get(numero.intValue() - 1);
		return new Linha(numero.intValue(), string);
	}
}