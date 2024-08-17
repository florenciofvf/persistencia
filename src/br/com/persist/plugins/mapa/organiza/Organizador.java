package br.com.persist.plugins.mapa.organiza;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.plugins.mapa.MapaException;
import br.com.persist.plugins.mapa.Objeto;

public abstract class Organizador {
	private final String id;

	protected Organizador(String id) {
		this.id = id;
	}

	public abstract void parametros(String string) throws MapaException, ArgumentoException;

	public abstract void organizar(Objeto objeto);

	public void reiniciar() {
	}

	@Override
	public String toString() {
		return id;
	}

	public static Organizador get(Objeto objeto) {
		return OrganizadorCache.get(objeto);
	}

	public static Organizador get(String nome) {
		return OrganizadorCache.get(nome);
	}

	public static class OrganizadorCache {
		private static final Map<String, Organizador> cache = new HashMap<>();
		private static final Organizador padrao = new OrganizadorRandomico();

		private OrganizadorCache() {
		}

		public static Organizador get(Objeto objeto) {
			return objeto.getOrganizador() != null ? objeto.getOrganizador() : padrao;
		}

		public static Organizador get(String nome) {
			if (nome != null) {
				nome = nome.trim().toLowerCase();
			}
			return cache.get(nome);
		}

		static {
			Organizador ref = new OrganizadorSequencia();
			cache.put(ref.id, ref);
			ref = new OrganizadorRandomico();
			cache.put(ref.id, ref);
			ref = new OrganizadorCircular();
			cache.put(ref.id, ref);
			ref = new OrganizadorBola();
			cache.put(ref.id, ref);
		}
	}
}