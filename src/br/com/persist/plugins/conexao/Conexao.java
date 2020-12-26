package br.com.persist.plugins.conexao;

import java.sql.Connection;
import java.sql.DriverManager;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Conexao {
	private String finalComplemento;
	private final String nome;
	private String constraint;
	private String urlBanco;
	private String catalogo;
	private String usuario;
	private String esquema;
	private String driver;
	private String senha;

	public Conexao(String nome) {
		if (Util.estaVazio(nome)) {
			throw new IllegalArgumentException("Nome nulo.");
		}
		this.nome = nome;
	}

	Connection getConnection() throws ConexaoException {
		try {
			Class.forName(getDriver());
			return DriverManager.getConnection(getUrlBanco(), getUsuario(), getSenha());
		} catch (Exception ex) {
			throw new ConexaoException(ex);
		}
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

	public boolean isValido() {
		return !Util.estaVazio(nome);
	}

	public Conexao clonar(String novoNome) {
		Conexao c = new Conexao(novoNome);
		c.finalComplemento = finalComplemento;
		c.constraint = constraint;
		c.urlBanco = urlBanco;
		c.catalogo = catalogo;
		c.esquema = esquema;
		c.usuario = usuario;
		c.driver = driver;
		c.senha = senha;
		return c;
	}

	public void aplicar(Attributes attr) {
		finalComplemento = attr.getValue("finalComplemento");
		constraint = attr.getValue("constraint");
		urlBanco = attr.getValue("urlBanco");
		catalogo = attr.getValue("catalogo");
		esquema = attr.getValue("esquema");
		usuario = attr.getValue("usuario");
		driver = attr.getValue("driver");
		senha = attr.getValue("senha");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("conexao");
		util.atributo("nome", Util.escapar(nome));
		util.atributo("usuario", Util.escapar(usuario));
		util.atributo("senha", Util.escapar(senha));
		util.atributo("finalComplemento", Util.escapar(finalComplemento));
		util.atributo("constraint", Util.escapar(constraint));
		util.atributo("urlBanco", Util.escapar(urlBanco));
		util.atributo("catalogo", Util.escapar(catalogo));
		util.atributo("esquema", Util.escapar(esquema));
		util.atributo("driver", Util.escapar(driver));
		util.fecharTag().finalizarTag("conexao");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Conexao) {
			Conexao c = (Conexao) obj;
			return nome.equals(c.nome);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return nome.hashCode();
	}

	@Override
	public String toString() {
		return nome;
	}

	public String getFinalComplemento() {
		return finalComplemento;
	}

	public void setFinalComplemento(String finalComplemento) {
		this.finalComplemento = finalComplemento;
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public String getEsquema() {
		return esquema;
	}

	public void setEsquema(String esquema) {
		this.esquema = esquema;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}
}