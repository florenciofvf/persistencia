package br.com.persist.geradores;

public class ContainerJS extends Container {
	protected ContainerJS(String id) {
		super(id);
	}

	public JSFuncao criarJSFuncao(String nome, Parametros param) {
		JSFuncao funcao = new JSFuncao(nome, param);
		add(funcao);
		return funcao;
	}

	public JSFuncao criarJSFuncao(String nome) {
		return criarJSFuncao(nome, new Parametros());
	}

	public JSFuncaoAtributo criarJSFuncaoAtributo(String nome, Parametros param) {
		JSFuncaoAtributo funcao = new JSFuncaoAtributo(nome, param);
		add(funcao);
		return funcao;
	}

	public JSFuncaoAtributo criarJSFuncaoAtributo(String nome) {
		return criarJSFuncaoAtributo(nome, new Parametros());
	}

	public JSFuncaoPropriedade criarJSFuncaoPropriedade(boolean separar, String nome, Parametros param) {
		JSFuncaoPropriedade funcao = new JSFuncaoPropriedade(separar, nome, param);
		add(funcao);
		return funcao;
	}

	public JSFuncaoPropriedade criarJSFuncaoPropriedade(boolean separar, String nome) {
		return criarJSFuncaoPropriedade(separar, nome, new Parametros());
	}

	public JSVarObj criarJSVarObj(String nome) {
		JSVarObj obj = new JSVarObj(nome);
		add(obj);
		return obj;
	}

	public JSReturnObj criarJSReturnObj() {
		JSReturnObj obj = new JSReturnObj();
		add(obj);
		return obj;
	}

	public Container addJSAtributo(boolean separar, String nome, String valor) {
		add(new JSAtributo(separar, nome, valor));
		return this;
	}

	public Container addJSVar(String string) {
		add(new JSVar(string));
		return this;
	}
}