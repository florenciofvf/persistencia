package br.com.persist.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinkAuto {

	private LinkAuto() {
	}

	public static class Link {
		final List<Tabela> tabelas = new ArrayList<>();
		final String campo;

		public Link(String campo) {
			this.campo = campo;
		}

		public List<Tabela> getTabelas() {
			return tabelas;
		}

		public String getCampo() {
			return campo;
		}

		@Override
		public String toString() {
			return campo;
		}

		public String getDetalhe() {
			StringBuilder sb = new StringBuilder(campo + "=" + Constantes.QL);

			for (int i = 0; i < tabelas.size(); i++) {
				Tabela tabela = tabelas.get(i);
				sb.append(Constantes.TAB + tabela.descricao);

				if (i + 1 < tabelas.size()) {
					sb.append(",");
				}

				sb.append(Constantes.QL);
			}

			return sb.toString();
		}
	}

	public static class Tabela {
		final String descricao;
		final String apelido;
		final String campo;
		final String nome;

		public Tabela(String descricao) {
			this.descricao = descricao;
			int pos = descricao.indexOf('.');
			String n = descricao.substring(0, pos);

			if (n.startsWith("(")) {
				int pos2 = n.indexOf(')');
				apelido = n.substring(1, pos2);
				nome = n.substring(pos2 + 1);
			} else {
				apelido = "";
				nome = n;
			}

			campo = descricao.substring(pos + 1);
		}

		public String getApelido() {
			return apelido;
		}

		public String getCampo() {
			return campo;
		}

		public String getNome() {
			return nome;
		}
	}

	public static List<Link> criarLinksAuto(String string) {
		Map<String, Link> mapa = new LinkedHashMap<>();

		if (!Util.estaVazio(string)) {
			String[] links = string.split(";");

			if (links != null) {
				for (String link : links) {
					processarLink(link, mapa);
				}
			}
		}

		return new ArrayList<>(mapa.values());
	}

	private static void processarLink(String stringLink, Map<String, Link> mapa) {
		String[] linkTabelas = stringLink.split("=");

		if (linkTabelas != null && linkTabelas.length > 1) {
			String campo = linkTabelas[0].trim();

			Link link = mapa.get(campo);

			if (link == null) {
				link = new Link(campo);
				mapa.put(campo, link);
			} else {
				link.tabelas.clear();
			}

			String stringTabelas = linkTabelas[1];
			String[] tabelas = stringTabelas.split(",");

			for (String stringTabela : tabelas) {
				link.tabelas.add(new Tabela(stringTabela.trim()));
			}
		}
	}
}