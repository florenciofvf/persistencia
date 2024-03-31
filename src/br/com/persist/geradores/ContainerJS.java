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

	public JSInvocaProm criarJSInvocaProm(String string) {
		JSInvocaProm obj = new JSInvocaProm(string);
		add(obj);
		return obj;
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

	public Container addJSVar(String string) {
		add(new JSVar(string));
		return this;
	}
}