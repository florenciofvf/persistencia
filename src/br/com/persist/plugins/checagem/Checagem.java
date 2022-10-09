package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public class Checagem {
	private final List<Modulo> modulos;
	private final List<String> imports;

	public Checagem() {
		modulos = new ArrayList<>();
		imports = new ArrayList<>();
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

	public String executar(String idModulo, String idBloco, Contexto ctx) {
		if (idModulo == null) {
			return "idModulo null";
		}
		if (ctx == null) {
			return "ctx null";
		}
		Modulo modulo = getModulo(idModulo);
		if (modulo == null) {
			return "Modulo inexistente! >>> " + idModulo;
		}
		return modulo.executar(this, idBloco, ctx);
	}

	public Bloco getBlocoImportado(String idBloco) throws ChecagemException {
		for (String idModulo : imports) {
			ChecagemUtil.checarModulo(idModulo);
			Modulo modulo = getModulo(idModulo);
			Bloco bloco = modulo.getBloco(idBloco);
			if (bloco != null) {
				return bloco;
			}
		}
		return null;
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

	public void add(String modulo) {
		if (modulo != null && !modulo.trim().isEmpty() && !imports.contains(modulo)) {
			imports.add(modulo);
		}
	}

	public void set(Modulo modulo) {
		if (modulo != null && modulos.contains(modulo)) {
			modulos.remove(modulo);
		}
		modulos.add(modulo);
	}
}