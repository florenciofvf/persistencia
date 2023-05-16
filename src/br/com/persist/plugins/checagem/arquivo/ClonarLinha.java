package br.com.persist.plugins.checagem.arquivo;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnariaOuNParam;

public class ClonarLinha extends FuncaoUnariaOuNParam {
	private static final String ERRO = "Erro ClonarLinha";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		if (!(op0 instanceof Linha)) {
			throw new ChecagemException(getClass(), ERRO + " >>> op0 deve ser Linha");
		}
		Linha linha = (Linha) op0;
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < parametros.size(); i++) {
			Object valor = parametros.get(i).executar(checagem, bloco, ctx);
			sb.append(valor == null ? "null" : valor.toString());
		}
		if (sb.length() > 0) {
			return new Linha(linha.numero, sb.toString());
		}
		return linha;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "clonarLinha(Linha, [Texto, Texto]) : Linha";
	}
}