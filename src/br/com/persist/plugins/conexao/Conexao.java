package br.com.persist.plugins.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Conexao {
	private Map<String, String> mapaTiposFuncoes;
	private String tiposFuncoes;
	private final String nome;
	private String constraint;
	private String urlBanco;
	private String catalogo;
	private String usuario;
	private String esquema;
	private String driver;
	private String filtro;
	private String senha;
	private String grupo;
	private String limit;

	public Conexao(String nome) throws ArgumentoException {
		if (Util.isEmpty(nome)) {
			throw new ArgumentoException("Nome nulo.");
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
		return !Util.isEmpty(nome);
	}

	public Conexao clonar(String novoNome) throws ArgumentoException {
		Conexao c = new Conexao(novoNome);
		c.tiposFuncoes = tiposFuncoes;
		c.constraint = constraint;
		c.urlBanco = urlBanco;
		c.catalogo = catalogo;
		c.esquema = esquema;
		c.usuario = usuario;
		c.driver = driver;
		c.filtro = filtro;
		c.senha = senha;
		c.grupo = grupo;
		c.limit = limit;
		return c;
	}

	public void aplicar(Attributes attr) {
		tiposFuncoes = attr.getValue("tiposFuncoes");
		constraint = attr.getValue("constraint");
		urlBanco = attr.getValue("urlBanco");
		catalogo = attr.getValue("catalogo");
		esquema = attr.getValue("esquema");
		usuario = attr.getValue("usuario");
		driver = attr.getValue("driver");
		filtro = attr.getValue("filtro");
		senha = attr.getValue("senha");
		grupo = attr.getValue("grupo");
		limit = attr.getValue("limit");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("conexao");
		util.atributo("nome", nome);
		util.atributo("usuario", usuario);
		util.atributo("senha", senha);
		util.atributo("filtro", filtro);
		util.atributo("constraint", constraint);
		util.atributo("urlBanco", urlBanco);
		util.atributo("catalogo", catalogo);
		util.atributo("esquema", esquema);
		util.atributo("driver", driver);
		util.atributoCheck("grupo", grupo);
		util.atributoCheck("limit", limit);
		util.atributo("tiposFuncoes", tiposFuncoes);
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

	public String getFiltro() {
		return filtro;
	}

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	public String getTiposFuncoes() {
		return tiposFuncoes;
	}

	public void setTiposFuncoes(String tiposFuncoes) {
		this.tiposFuncoes = tiposFuncoes;
	}

	public String getGrupo() {
		if (grupo == null) {
			grupo = Constantes.VAZIO;
		}
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getLimit() {
		if (limit == null) {
			limit = Constantes.VAZIO;
		}
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public Map<String, String> getMapaTiposFuncoes() {
		if (mapaTiposFuncoes == null) {
			mapaTiposFuncoes = ConexaoUtil.criarMapaTiposFuncoes(tiposFuncoes);
		}
		return mapaTiposFuncoes;
	}

	public void setMapaTiposFuncoes(Map<String, String> mapaTiposFuncoes) {
		this.mapaTiposFuncoes = mapaTiposFuncoes;
	}

	public String getLimite() {
		return getLimit();
	}
}