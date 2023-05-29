package br.com.persist.plugins.checagem.arquivo;

import java.util.List;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinaria;

public class LinhaPeloNumero extends FuncaoBinaria implements Arquivo {
	private static final String ERRO = "Erro LinhaPeloNumero";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checar(op0);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioLong(op1, ERRO + " >>> op1");
		Long numero = (Long) op1;
		checar(numero);
		List<String> arquivo = get(op0);
		checar(numero, arquivo);
		return criarLinha(numero, arquivo);
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "linhaPeloNumero(List<String>, Numero) : Linha";
	}
}