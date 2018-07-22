package br.com.persist.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

import br.com.persist.util.Util;
import br.com.persist.util.XMLUtil;

public class Conexao {
	private static Map<Conexao, Connection> mapa = new HashMap<>();
	private String urlBanco;
	private String usuario;
	private String driver;
	private String senha;
	private String nome;

	private Connection getConnection() throws Exception {
		Class.forName(getDriver());
		Connection conn = DriverManager.getConnection(getUrlBanco(), getUsuario(), getSenha());
		return conn;
	}

	public static synchronized Connection getConnection(Conexao conexao) throws Exception {
		Connection conn = mapa.get(conexao);

		if (conn == null || conn.isClosed()) {
			conn = conexao.getConnection();
			mapa.put(conexao, conn);
		}

		return conn;
	}

	private static void fecharConexao(Connection conn) throws Exception {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
	}

	public static void fecharConexoes() throws Exception {
		for (Connection conn : mapa.values()) {
			fecharConexao(conn);
		}
	}

	public static List<Conexao> getConexoes() {
		return new ArrayList<>(mapa.keySet());
	}

	public void setUrlBanco(String urlBanco) {
		this.urlBanco = urlBanco;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUrlBanco() {
		return urlBanco;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getDriver() {
		return driver;
	}

	public String getSenha() {
		return senha;
	}

	public String getNome() {
		return nome;
	}

	public boolean isValida() {
		return !Util.estaVazio(nome);
	}

	public Conexao clonar() {
		Conexao c = new Conexao();

		c.urlBanco = urlBanco;
		c.usuario = usuario;
		c.driver = driver;
		c.senha = senha;

		return c;
	}

	public void aplicar(Attributes attr) {
		urlBanco = attr.getValue("urlBanco");
		usuario = attr.getValue("usuario");
		driver = attr.getValue("driver");
		senha = attr.getValue("senha");
		nome = attr.getValue("nome");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("conexao");
		util.atributo("urlBanco", Util.escapar(urlBanco));
		util.atributo("usuario", Util.escapar(usuario));
		util.atributo("driver", Util.escapar(driver));
		util.atributo("senha", Util.escapar(senha));
		util.atributo("nome", Util.escapar(nome));
		util.fecharTag().finalizarTag("conexao");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Conexao) {
			Conexao con = (Conexao) obj;
			return !Util.estaVazio(nome) && nome.equals(con.nome);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Util.estaVazio(nome) ? -1 : nome.hashCode();
	}

	@Override
	public String toString() {
		return nome;
	}
}