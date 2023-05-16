package br.com.persist.plugins.checagem.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoTernaria;

public class LinhasEntreIniFim extends FuncaoTernaria implements Arquivo {
	private static final String ERRO = "Erro LinhasEntreIniFim";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checar(op0);
		Object op1 = param1().executar(checagem, bloco, ctx);
		Object op2 = param2().executar(checagem, bloco, ctx);
		checkObrigatorioString(op1, ERRO + " >>> op1");
		checkObrigatorioString(op2, ERRO + " >>> op2");
		String strInicio = ((String) op1).trim();
		String strFinal = ((String) op2).trim();
		if (Util.estaVazio(strInicio)) {
			throw new ChecagemException(getClass(), ERRO + " >>> op1 vazio");
		}
		if (Util.estaVazio(strFinal)) {
			throw new ChecagemException(getClass(), ERRO + " >>> op2 vazio");
		}
		List<Linha> coletor = new ArrayList<>();
		List<String> arquivo = get(op0);
		LinhasPelaStringIniFim.linhasStrIniStrFim(strInicio, strFinal, coletor, arquivo);
		if (coletor.isEmpty()) {
			lancarExcecao(getClass(), ERRO, ctx, strInicio, strFinal);
		}
		List<Linha> resposta = new ArrayList<>();
		for (Linha linha : coletor) {
			String string = linha.string;
			int posIni = string.indexOf(strInicio);
			int posFim = string.indexOf(strFinal);
			String nova = string.substring(posIni + strInicio.length(), posFim);
			resposta.add(new Linha(linha.numero, nova));
		}
		return resposta;
	}

	static void lancarExcecao(Class<?> klass, String erro, Contexto ctx, String strInicio, String strFinal)
			throws ChecagemException {
		Object object = ctx.get("absoluto");
		String absoluto = "";
		if (object instanceof String) {
			absoluto = "Arquivo: " + (String) object;
		}
		throw new ChecagemException(klass, erro + " - Nenhuma linha come\u00E7ando com <<<[" + strInicio
				+ "]>>> e finalizando com <<<[" + strFinal + "]>>> " + absoluto);
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "linhasEntreIniFim(List<String>, Texto, Texto) : List<Linha>";
	}
}