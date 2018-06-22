package br.com.persistencia.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import br.com.persistencia.util.Constantes;

public class Conexao {
	private static Map<String, String> mapa = new HashMap<>();
	private static Connection conn;

	public static synchronized Connection getConnection() throws Exception {
		if (mapa.isEmpty()) {
			inicializar();
		}

		if (conn == null || conn.isClosed()) {
			Class.forName(getValor(Constantes.DRIVER));

			String url = getValor(Constantes.URL);
			String usr = getValor(Constantes.LOGIN);
			String psw = getValor(Constantes.SENHA);

			conn = DriverManager.getConnection(url, usr, psw);
		}

		return conn;
	}

	public static void close() throws Exception {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
	}

	public static String getValor(String chave) {
		return mapa.get(chave);
	}

	public static void setValor(String chave, String valor) {
		if (chave == null) {
			return;
		}

		mapa.put(chave, valor);
	}

	private static void inicializar() {
		ResourceBundle bundle = ResourceBundle.getBundle("conexao");

		Enumeration<String> enumeration = bundle.getKeys();

		while (enumeration.hasMoreElements()) {
			String chave = enumeration.nextElement();
			String valor = bundle.getString(chave);

			mapa.put(chave, valor);
		}
	}
}