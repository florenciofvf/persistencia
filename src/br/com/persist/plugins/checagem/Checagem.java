package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public class Checagem {
	private final List<Modulo> modulos;

	public Checagem() {
		modulos = new ArrayList<>();
	}

	public List<Object> processar(String idModulo, String idBloco, Contexto ctx) throws ChecagemException {
		if (idModulo == null) {
			throw new ChecagemException(getClass(), "idModulo null");
		}
		if (ctx == null) {
			throw new ChecagemException(getClass(), "ctx null");
		}
		Modulo modulo = getModulo(idModulo);
		if (modulo == null) {
			throw new ChecagemException(getClass(), "modulo null");
		}
		return modulo.processar(this, idBloco, ctx);
	}

	public Modulo getModulo(String idModulo) {
		if (idModulo == null) {
			return null;
		}
		for (Modulo modulo : modulos) {
			if (idModulo.equalsIgnoreCase(modulo.getId())) {
				return modulo;
			}
		}
		return null;
	}

	public void add(Modulo modulo) {
		if (modulo != null && !modulos.contains(modulo)) {
			modulos.add(modulo);
		}
	}

	public void set(Modulo modulo) {
		if (modulo != null && modulos.contains(modulo)) {
			modulos.remove(modulo);
		}
		modulos.add(modulo);
	}
}