package br.com.persist.plugins.checagem.arquivo;

import java.util.List;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinaria;

public class GetLinha extends FuncaoBinaria implements Arquivo {
	private static final String ERRO = "Erro GetLinha";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checar2(op0);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioLong(op1, ERRO + " >>> op1");
		Long numero = (Long) op1;
		checar(numero);
		List<Linha> arquivo = get2(op0);
		checar2(numero, arquivo);
		return arquivo.get(numero.intValue() - 1);
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "getLinha(List<Linha>, Numero) : Linha";
	}
}