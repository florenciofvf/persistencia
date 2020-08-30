package br.com.persist.xml;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.conexao.Conexao;
import br.com.persist.fragmento.Fragmento;
import br.com.persist.objeto.Form;
import br.com.persist.objeto.Objeto;
import br.com.persist.relacao.Relacao;
import br.com.persist.util.MenuApp;

public class XMLColetor {
	private final AtomicBoolean ajusteAutoForm;
	private final List<Fragmento> fragmentos;
	private final StringBuilder sbConexao;
	private final List<Relacao> relacoes;
	private final List<Conexao> conexoes;
	private final List<Objeto> objetos;
	private final Dimension dimension;
	private final List<MenuApp> menus;
	private final List<Form> forms;

	public XMLColetor() {
		ajusteAutoForm = new AtomicBoolean();
		sbConexao = new StringBuilder();
		fragmentos = new ArrayList<>();
		dimension = new Dimension();
		relacoes = new ArrayList<>();
		conexoes = new ArrayList<>();
		objetos = new ArrayList<>();
		forms = new ArrayList<>();
		menus = new ArrayList<>();
	}

	public void init() {
		if (sbConexao.length() > 0) {
			sbConexao.delete(0, sbConexao.length());
		}
		ajusteAutoForm.set(false);
		dimension.setSize(0, 0);
		fragmentos.clear();
		conexoes.clear();
		relacoes.clear();
		objetos.clear();
		forms.clear();
		menus.clear();
	}

	public AtomicBoolean getAjusteAutoForm() {
		return ajusteAutoForm;
	}

	public List<Fragmento> getFragmentos() {
		return fragmentos;
	}

	public StringBuilder getSbConexao() {
		return sbConexao;
	}

	public List<Conexao> getConexoes() {
		return conexoes;
	}

	public List<Relacao> getRelacoes() {
		return relacoes;
	}

	public List<Objeto> getObjetos() {
		return objetos;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public List<MenuApp> getMenus() {
		return menus;
	}

	public List<Form> getForms() {
		return forms;
	}
}