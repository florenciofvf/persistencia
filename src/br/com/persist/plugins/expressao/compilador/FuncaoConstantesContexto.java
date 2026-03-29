package br.com.persist.plugins.expressao.compilador;

public class FuncaoConstantesContexto extends FuncaoContexto {
	private RetornoContexto retornoContexto = new RetornoContexto();

	public FuncaoConstantesContexto() {
		add(retornoContexto);
	}

	@Override
	public void add(Contexto c) {
		remove(retornoContexto);
		super.add(c);
		add(retornoContexto);
	}
}