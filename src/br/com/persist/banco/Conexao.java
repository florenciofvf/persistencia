package br.com.persist.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import br.com.persist.util.Util;
import br.com.persist.util.XMLUtil;

public class Conexao {
	private static final Logger LOG = Logger.getGlobal();
	private static final Map<Conexao, Connection> CONEXOES = new HashMap<>();
	private String inicioComplemento;
	private String finalComplemento;
	private String urlBanco;
	private String usuario;
	private String esquema;
	private String driver;
	private String senha;
	private String nome;

	private Connection getConnection() throws Exception {
		Class.forName(getDriver());
		return DriverManager.getConnection(getUrlBanco(), getUsuario(), getSenha());
	}

	public static synchronized Connection getConnection(Conexao conexao) throws Exception {
		Connection conn = CONEXOES.get(conexao);

		if (conn == null || conn.isClosed()) {
			conn = conexao.getConnection();
			CONEXOES.put(conexao, conn);
		}

		return conn;
	}

	public static synchronized Connection getConnection2(Conexao conexao) throws Exception {
		Connection conn = CONEXOES.get(conexao);

		if (conn != null) {
			try {
				fecharConexao(conn);
				CONEXOES.put(conexao, null);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "ERRO", e);
			}
		}

		return getConnection(conexao);
	}

	private static void fecharConexao(Connection conn) throws Exception {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
	}

	public static void fecharConexoes() throws Exception {
		for (Connection conn : CONEXOES.values()) {
			fecharConexao(conn);
		}
	}

	public void fechar() {
		Connection conn = CONEXOES.get(this);

		if (conn != null) {
			try {
				fecharConexao(conn);
				CONEXOES.put(this, null);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "ERRO", e);
			}
		}
	}

	public static List<Conexao> getConexoes() {
		return new ArrayList<>(CONEXOES.keySet());
	}

	public static Connection get(Conexao conexao) {
		return CONEXOES.get(conexao);
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

		c.inicioComplemento = inicioComplemento;
		c.finalComplemento = finalComplemento;
		c.urlBanco = urlBanco;
		c.esquema = esquema;
		c.usuario = usuario;
		c.driver = driver;
		c.senha = senha;

		return c;
	}

	public void aplicar(Attributes attr) {
		inicioComplemento = attr.getValue("inicioComplemento");
		finalComplemento = attr.getValue("finalComplemento");
		urlBanco = attr.getValue("urlBanco");
		esquema = attr.getValue("esquema");
		usuario = attr.getValue("usuario");
		driver = attr.getValue("driver");
		senha = attr.getValue("senha");
		nome = attr.getValue("nome");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("conexao");
		util.atributo("inicioComplemento", Util.escapar(inicioComplemento));
		util.atributo("finalComplemento", Util.escapar(finalComplemento));
		util.atributo("urlBanco", Util.escapar(urlBanco));
		util.atributo("esquema", Util.escapar(esquema));
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

	public String getInicioComplemento() {
		return inicioComplemento;
	}

	public void setInicioComplemento(String inicioComplemento) {
		this.inicioComplemento = inicioComplemento;
	}

	public String getFinalComplemento() {
		return finalComplemento;
	}

	public void setFinalComplemento(String finalComplemento) {
		this.finalComplemento = finalComplemento;
	}

	public String getEsquema() {
		return esquema;
	}

	public void setEsquema(String esquema) {
		this.esquema = esquema;
	}
}