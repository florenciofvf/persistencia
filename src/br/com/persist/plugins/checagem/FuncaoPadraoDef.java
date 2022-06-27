package br.com.persist.plugins.checagem;

public class FuncaoPadraoDef extends FuncaoUnaria {
	private static final String ERRO = "Erro FuncaoPadraoDef";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		ChecagemGramatica.map.put((String) op0, FuncaoPadrao.class.getName());
		return null;
	}

	@Override
	public void encerrar() throws ChecagemException {
		super.encerrar();
		executar(null);
	}
}