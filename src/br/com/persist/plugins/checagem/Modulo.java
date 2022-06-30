package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public class Modulo {
	private final List<Bloco> blocos;
	private final String id;

	public Modulo(String id) {
		blocos = new ArrayList<>();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<Bloco> getBlocos() {
		return blocos;
	}

	public Bloco getBloco(String id) {
		if (id == null) {
			return null;
		}
		for (Bloco bloco : blocos) {
			if (id.equalsIgnoreCase(bloco.getId())) {
				return bloco;
			}
		}
		return null;
	}

	public void add(Bloco bloco) {
		if (bloco != null) {
			blocos.add(bloco);
		}
	}

	public void addBlocos(List<Bloco> blocos) {
		if (blocos != null) {
			for (Bloco bloco : blocos) {
				add(bloco);
			}
		}
	}

	public List<Object> executar(Checagem checagem, Contexto ctx) throws ChecagemException {
		List<Object> resp = new ArrayList<>();
		for (Bloco bloco : blocos) {
			resp.add(bloco.executar(checagem, ctx));
		}
		return resp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Modulo other = (Modulo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}