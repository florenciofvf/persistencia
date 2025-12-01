package br.com.persist.plugins.conexao;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetValor.Valor;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;

public class ConexaoProvedor {
	private static final Map<Conexao, Connection> CONEXOES = new HashMap<>();
	private static final List<Conexao> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private ConexaoProvedor() {
	}

	static {
		file = new File(ConexaoConstantes.CONEXOES + Constantes.SEPARADOR + "conexoes.xml");
	}

	public static int primeiro(int indice) {
		if (indice > 0 && indice < getSize()) {
			Conexao item = lista.remove(indice);
			lista.add(0, item);
			return 0;
		}
		return -1;
	}

	public static int anterior(int indice) {
		if (indice > 0 && indice < getSize()) {
			Conexao item = lista.remove(indice);
			indice--;
			lista.add(indice, item);
			return indice;
		}
		return -1;
	}

	public static int proximo(int indice) {
		if (indice + 1 < getSize()) {
			Conexao item = lista.remove(indice);
			indice++;
			lista.add(indice, item);
			return indice;
		}
		return -1;
	}

	public static void excluir(int indice) {
		if (indice >= 0 && indice < getSize()) {
			lista.remove(indice);
		}
	}

	public static Conexao getConexao(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}
		return null;
	}

	public static Conexao getConexao(String nome) {
		for (Conexao item : lista) {
			if (item.getNome().equals(nome)) {
				return item;
			}
		}
		return null;
	}

	public static int getIndice(String nome) {
		for (int i = 0; i < lista.size(); i++) {
			Conexao item = lista.get(i);
			if (item.getNome().equals(nome)) {
				return i;
			}
		}
		return -1;
	}

	public static int getSize() {
		return lista.size();
	}

	public static boolean contem(Conexao conexao) {
		return contem(conexao.getNome());
	}

	public static boolean contem(String nome) {
		return getConexao(nome) != null;
	}

	public static void adicionar(Conexao conexao) {
		if (contem(conexao)) {
			return;
		}
		lista.add(conexao);
	}

	public static void inicializar() {
		lista.clear();
		try {
			ConexaoColetor coletor = new ConexaoColetor();
			if (file.exists() && file.canRead()) {
				XML.processar(file, new ConexaoHandler(coletor));
			}
			lista.addAll(coletor.getConexoes());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void salvar() throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2(ConexaoConstantes.CONEXOES);
		salvarConexoes(util);
		util.finalizarTag(ConexaoConstantes.CONEXOES);
		util.close();
	}

	private static void salvarConexoes(XMLUtil util) {
		for (Conexao item : lista) {
			if (item.isValido()) {
				item.salvar(util);
			}
		}
	}

	public static synchronized Connection getConnection(Conexao conexao) throws ConexaoException {
		try {
			Connection conn = CONEXOES.get(conexao);
			if (conn == null || !conn.isValid(1000) || conn.isClosed()) {
				conn = conexao.getConnection();
				CONEXOES.put(conexao, conn);
			}
			Preferencias.setErroCriarConnection(false);
			return conn;
		} catch (Exception ex) {
			Preferencias.setErroCriarConnection(true);
			throw new ConexaoException(ex);
		}
	}

	public static synchronized Connection getConnection2(Conexao conexao) throws ConexaoException {
		Connection conn = CONEXOES.get(conexao);
		if (conn != null) {
			try {
				fecharConexao(conn);
				CONEXOES.put(conexao, null);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
		return getConnection(conexao);
	}

	private static void fecharConexao(Connection conn) throws ConexaoException {
		try {
			if (conn != null && conn.isValid(1000) && !conn.isClosed()) {
				conn.close();
			}
		} catch (Exception ex) {
			throw new ConexaoException(ex);
		}
	}

	public static void fecharConexoes() throws ConexaoException {
		for (Connection conn : CONEXOES.values()) {
			fecharConexao(conn);
		}
	}

	public static void fechar(Conexao conexao) {
		Connection conn = CONEXOES.get(conexao);
		if (conn != null) {
			try {
				fecharConexao(conn);
				CONEXOES.put(conexao, null);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
	}

	public static Connection get(Conexao conexao) {
		return CONEXOES.get(conexao);
	}

	public static JComboBox<Conexao> criarComboConexao(Conexao padrao) {
		Combo combo = new Combo(new ConexaoComboModelo(lista));
		combo.setSelectedItem(padrao);
		return combo;
	}

	private static class Combo extends JComboBox<Conexao> implements PopupMenuListener {
		private static final long serialVersionUID = 1L;
		private int total;

		public Combo(ConexaoComboModelo modelo) {
			super(modelo);
			total = modelo.getSize();
			addPopupMenuListener(this);
			if (total > 0) {
				setSelectedIndex(0);
			}
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			if (total != getModel().getSize()) {
				total = getModel().getSize();
				((ConexaoComboModelo) getModel()).notificarMudancas();
			}
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			LOG.log(Level.FINEST, "popupMenuWillBecomeInvisible");
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			LOG.log(Level.FINEST, "popupMenuCanceled");
		}
	}

	public static Valor getValorUrl(int i) {
		Conexao conexao = getConexao(i);
		return new ValorURL(conexao);
	}

	private static class ValorURL implements Valor {
		private final Conexao conexao;

		private ValorURL(Conexao conexao) {
			this.conexao = conexao;
		}

		@Override
		public String getTitle() {
			return "URL do Banco";
		}

		@Override
		public String get() {
			return conexao.getUrlBanco();
		}

		@Override
		public void set(String s) {
			conexao.setUrlBanco(s);
		}
	}

	public static void contemConteudo(Set<String> set, String string, boolean porParte) {
		for (Conexao item : lista) {
			if (Util.existeEm(item.getUrlBanco(), string, porParte)) {
				set.add(item.getNome());
			}
		}
	}
}