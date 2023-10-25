package br.com.persist.plugins.objeto.internal;

import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.vinculo.Coletor;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.tabela.TabelaPersistencia;

public class InternalUtil {
	private InternalUtil() {
	}

	public static void atualizarColetores(TabelaPersistencia tabela, int coluna, Referencia referencia) {
		OrdenacaoModelo modelo = tabela.getModelo();
		int total = modelo.getRowCount();
		for (int i = 0; i < total; i++) {
			Object obj = modelo.getValueAt(i, coluna);
			if (obj != null && !Util.estaVazio(obj.toString())) {
				referencia.atualizarColetores(obj.toString());
			}
		}
	}

	public static void consolidarColetores(TabelaPersistencia tabela, int linha, int coluna, Pesquisa pesquisa) {
		List<Object> registro = tabela.getModelo().getRegistro(linha);
		String valor = registro.get(coluna).toString();
		StringBuilder builder = new StringBuilder();
		final String espaco = "   ";
		for (Referencia ref : pesquisa.getReferencias()) {
			Coletor coletor = ref.getColetor(valor);
			if (coletor.getTotal() > 0) {
				builder.append(ref.getTabela() + " [" + coletor.getTotal() + "]" + espaco);
			}
		}
		if (builder.length() > 0) {
			builder.delete(builder.length() - espaco.length(), builder.length());
		}
		registro.set(registro.size() - 1, builder.toString());
	}

	public static String campoExportadoPara(String tabelaInvocador, String campoInvocador, List<String> lista) {
		Invocador invocador = new Invocador(tabelaInvocador, campoInvocador);
		StringBuilder sb = new StringBuilder();
		for (String string : lista) {
			if (sb.length() > 0) {
				sb.append(Constantes.QL2);
			}
			sb.append(string + Constantes.QL);
			sb.append(Util.completar("", string.length(), '-') + Constantes.QL);
			TabelaCampo tabelaCampo = new TabelaCampo(invocador, string);
			sb.append(tabelaCampo.getOneToMany());
		}
		return sb.toString();
	}

	public static String campoImportadoDe(String tabelaInvocador, String campoInvocador, List<String> lista) {
		Invocador invocador = new Invocador(tabelaInvocador, campoInvocador);
		StringBuilder sb = new StringBuilder();
		for (String string : lista) {
			if (sb.length() > 0) {
				sb.append(Constantes.QL2);
			}
			sb.append(string + Constantes.QL);
			sb.append(Util.completar("", string.length(), '-') + Constantes.QL);
			TabelaCampo tabelaCampo = new TabelaCampo(invocador, string);
			sb.append(tabelaCampo.getOneToOne() + Constantes.QL);
			sb.append(tabelaCampo.getManyToOne());
		}
		return sb.toString();
	}

	static String lower(String s) {
		return s.toLowerCase();
	}

	static String lower2(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
}

class Invocador {
	final String tabela;
	final String campo;

	public Invocador(String tabela, String campo) {
		this.tabela = tabela;
		this.campo = campo;
	}
}

class TabelaCampo {
	private final Invocador invocador;
	private final String tabela;
	private final String campo;

	public TabelaCampo(Invocador invocador, String tabelaCampo) {
		this.invocador = invocador;
		int pos = tabelaCampo.indexOf('(');
		int pos2 = tabelaCampo.indexOf(')');
		tabela = tabelaCampo.substring(0, pos);
		campo = tabelaCampo.substring(pos + 1, pos2);
	}

	String getOneToOne() {
		StringBuilder sb = new StringBuilder("@OneToOne" + Constantes.QL);
		sb.append(new JoinColumn(invocador.campo, campo).toString());
		sb.append("private " + InternalUtil.lower2(tabela) + " " + InternalUtil.lower(tabela) + ";" + Constantes.QL);
		return sb.toString();
	}

	String getOneToMany() {
		StringBuilder sb = new StringBuilder("@OneToMany" + Constantes.QL);
		sb.append(new JoinColumn(invocador.campo, campo).toString());
		sb.append("private List<" + InternalUtil.lower2(tabela) + "> " + InternalUtil.lower(tabela) + "s;"
				+ Constantes.QL);
		return sb.toString();
	}

	String getManyToOne() {
		StringBuilder sb = new StringBuilder("@ManyToOne" + Constantes.QL);
		sb.append(new JoinColumn(invocador.campo, campo).toString());
		sb.append("private " + InternalUtil.lower2(tabela) + " " + InternalUtil.lower(tabela) + ";" + Constantes.QL);
		return sb.toString();
	}
}

class JoinColumn {
	String nome;
	String referenciado;

	JoinColumn(String nome, String referenciado) {
		this.nome = nome;
		this.referenciado = referenciado;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("@JoinColumn(name = ");
		sb.append(Util.citar2(nome));
		sb.append(", referencedColumnName = ");
		sb.append(Util.citar2(referenciado));
		sb.append(")" + Constantes.QL);
		return sb.toString();
	}
}