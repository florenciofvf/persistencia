package br.com.persist.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BuscaAuto {

	private BuscaAuto() {
	}

	public static class Grupo {
		final List<Tabela> tabelas = new ArrayList<>();
		final String descricao;
		final String campo;

		public Grupo(String descricao) {
			this.campo = descricao.substring(descricao.indexOf('.') + 1);
			this.descricao = descricao;
		}

		public void setArgumentos(List<String> argumentos) {
			for (Tabela tabela : tabelas) {
				tabela.setArgumentos(argumentos);
			}
		}

		public void setProcessado(boolean b) {
			for (Tabela tabela : tabelas) {
				tabela.setProcessado(b);
			}
		}

		public boolean isProcessado() {
			for (Tabela tabela : tabelas) {
				if (tabela.isProcessado()) {
					return true;
				}
			}

			return false;
		}

		public List<Tabela> getTabelas() {
			return tabelas;
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

		public String getDetalhe() {
			StringBuilder sb = new StringBuilder(descricao + "=" + Constantes.QL);

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
		List<Contabil> contabilizados;
		List<String> argumentos;
		final String descricao;
		final String apelido;
		final String campo;
		boolean processado;
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

		public void setArgumentos(List<String> argumentos) {
			this.argumentos = argumentos;

			if (argumentos != null) {
				contabilizados = new ArrayList<>();

				for (String string : argumentos) {
					contabilizados.add(new Contabil(string));
				}
			} else {
				contabilizados = null;
			}
		}

		public void setProcessado(boolean processado) {
			this.processado = processado;
		}

		public void contabilizar(String valor) {
			if (contabilizados == null) {
				return;
			}

			for (Contabil c : contabilizados) {
				if (c.tag.equals(valor)) {
					c.valor++;
				}
			}
		}

		public Contabil getContabil(String tag) {
			for (Contabil c : contabilizados) {
				if (c.tag.equals(tag)) {
					return c;
				}
			}

			return null;
		}

		public List<String> getArgumentos() {
			return argumentos;
		}

		public boolean isProcessado() {
			return processado;
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

		public static class Contabil {
			final String tag;
			int valor;

			public Contabil(String tag) {
				this.tag = tag;
			}

			public int getValor() {
				return valor;
			}
		}
	}

	public static List<Grupo> criarGruposAuto(String string) {
		Map<String, Grupo> mapa = new LinkedHashMap<>();

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