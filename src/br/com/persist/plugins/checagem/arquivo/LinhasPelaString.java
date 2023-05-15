package br.com.persist.plugins.checagem.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinaria;

public class LinhasPelaString extends FuncaoBinaria {
	private static final String ERRO = "Erro LinhasPelaString";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		if (!(op0 instanceof List<?>)) {
			throw new ChecagemException(getClass(), ERRO + " >>> op0 deve ser List<String>");
		}
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioString(op1, ERRO + " >>> op1");
		String string = ((String) op1).trim();
		List<Linha> resposta = new ArrayList<>();
		if (Util.estaVazio(string)) {
			return resposta;
		}
		List<String> arquivo = LinhaPeloNumero.get((List<?>) op0);
		for (int i = 0; i < arquivo.size(); i++) {
			String str = arquivo.get(i).trim();
			if (str.equals(string)) {
				resposta.add(new Linha(i + 1, arquivo.get(i)));
			}
		}
		return resposta;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "linhasPelaString(List<String>, Texto) : List<Linha>";
	}
}