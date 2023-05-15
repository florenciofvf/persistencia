package br.com.persist.plugins.checagem.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoTernaria;

public class LinhasPelaStringIniFim extends FuncaoTernaria {
	private static final String ERRO = "Erro LinhasPelaStringIniFim";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		if (!(op0 instanceof List<?>)) {
			throw new ChecagemException(getClass(), ERRO + " >>> op0 deve ser List<String>");
		}
		Object op1 = param1().executar(checagem, bloco, ctx);
		Object op2 = param2().executar(checagem, bloco, ctx);
		checkObrigatorioString(op1, ERRO + " >>> op1");
		checkObrigatorioString(op2, ERRO + " >>> op2");
		String strInicio = ((String) op1).trim();
		String strFinal = ((String) op2).trim();
		List<Linha> resposta = new ArrayList<>();
		if (Util.estaVazio(strInicio) || Util.estaVazio(strFinal)) {
			return resposta;
		}
		List<String> arquivo = LinhaPeloNumero.get((List<?>) op0);
		linhasStrIniStrFim(strInicio, strFinal, resposta, arquivo);
		return resposta;
	}

	static void linhasStrIniStrFim(String strInicio, String strFinal, List<Linha> resposta, List<String> arquivo) {
		for (int i = 0; i < arquivo.size(); i++) {
			String string = arquivo.get(i).trim();
			if (string.startsWith(strInicio) && string.endsWith(strFinal)) {
				resposta.add(new Linha(i + 1, arquivo.get(i)));
			}
		}
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "linhasPelaStringIniFim(List<String>, Texto, Texto) : List<Linha>";
	}
}