package br.com.persist.plugins.checagem.banco;

import java.util.Collection;
import java.util.Date;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinariaOuNParam;

public class SelectLog extends FuncaoBinariaOuNParam {
	private static final String ERRO = "Erro SelectLog";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		String instrucao = (String) op0;
		for (int i = 1; i < parametros.size(); i += 2) {
			Object nomeParametro = parametros.get(i).executar(checagem, bloco, ctx);
			checkObrigatorioString(nomeParametro, ERRO + " >>> op" + i);
			int indiceValor = i + 1;
			if (indiceValor >= parametros.size()) {
				throw new ChecagemException("Parametro sem valor >>> " + nomeParametro);
			}
			Object valorParametro = parametros.get(indiceValor).executar(checagem, bloco, ctx);
			instrucao = substituirParametro(instrucao, (String) nomeParametro, valorParametro);
		}
		return instrucao;
	}

	private String substituirParametro(String instrucao, String nomeParametro, Object valorParametro) {
		String normalizado = normalizar(valorParametro);
		return Util.replaceAll(instrucao, nomeParametro, normalizado);
	}

	private String normalizar(Object valorParametro) {
		if (valorParametro instanceof CharSequence || valorParametro instanceof Character
				|| valorParametro instanceof Date) {
			return "'" + valorParametro.toString() + "'";
		} else if (valorParametro instanceof Number) {
			return valorParametro.toString();
		} else if (valorParametro instanceof Collection<?>) {
			StringBuilder sb = new StringBuilder();
			Collection<?> colecao = (Collection<?>) valorParametro;
			for (Object object : colecao) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(normalizar(object));
			}
			return sb.toString();
		} else if (valorParametro != null) {
			return valorParametro.toString();
		}
		return "''";
	}
}