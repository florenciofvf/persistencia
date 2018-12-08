package br.com.persist.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuscaAuto {

	public static class Grupo {
		final List<Tabela> tabelas = new ArrayList<>();
		final String descricao;
		final String campo;

		public Grupo(String descricao) {
			this.campo = descricao.substring(descricao.indexOf('.') + 1);
			this.descricao = descricao;
		}

		public String getDescricao() {
			return descricao;
		}

		public String getCampo() {
			return campo;
		}

		@Override
		public String toString() {
			return descricao;
		}
	}

	public static class Tabela {
		final String nome;
		final String campo;

		public Tabela(String descricao) {
			int pos = descricao.indexOf('.');
			nome = descricao.substring(0, pos);
			campo = descricao.substring(pos + 1);
		}

		public String getCampo() {
			return campo;
		}

		public String getNome() {
			return nome;
		}
	}

	public static List<Grupo> criarGruposAuto(String string) {
		Map<String, Grupo> mapa = new HashMap<>();

		if (!Util.estaVazio(string)) {
			String[] grupos = string.split(";");

			if (grupos != null) {
				for (String grupo : grupos) {
					processarGrupo(grupo, mapa);
				}
			}
		}

		return new ArrayList<>(mapa.values());
	}

	private static void processarGrupo(String stringGrupo, Map<String, Grupo> mapa) {
		String[] grupoTabelas = stringGrupo.split("=");

		if (grupoTabelas != null && grupoTabelas.length > 1) {
			String descricao = grupoTabelas[0].trim();

			Grupo grupo = mapa.get(descricao);

			if (grupo == null) {
				grupo = new Grupo(descricao);
				mapa.put(descricao, grupo);
			} else {
				grupo.tabelas.clear();
			}

			String stringTabelas = grupoTabelas[1];
			String[] tabelas = stringTabelas.split(",");

			for (String stringTabela : tabelas) {
				grupo.tabelas.add(new Tabela(stringTabela.trim()));
			}
		}
	}
}