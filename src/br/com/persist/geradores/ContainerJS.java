package br.com.persist.geradores;

public class ContainerJS extends Container {
	protected ContainerJS(String id) {
		super(id);
	}

	public FuncaoJS criarFuncaoJS(String nome, Parametros param) {
		FuncaoJS funcao = new FuncaoJS(nome, param);
		add(funcao);
		return funcao;
	}

	public Container addVarJS(String string) {
		add(new VarJS(string));
		return this;
	}
}