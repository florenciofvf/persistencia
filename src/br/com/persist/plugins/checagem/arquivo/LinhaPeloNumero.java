package br.com.persist.plugins.checagem.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinaria;

public class LinhaPeloNumero extends FuncaoBinaria {
	private static final String ERRO = "Erro LinhaPeloNumero";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		if (!(op0 instanceof List<?>)) {
			throw new ChecagemException(getClass(), ERRO + " >>> op0 deve ser List<String>");
		}
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioLong(op1, ERRO + " >>> op1");
		Long numero = (Long) op1;
		if (numero < 1) {
			throw new ChecagemException(getClass(), ERRO + " >>> op1 numero menor que 1");
		}
		List<String> arquivo = get((List<?>) op0);
		if (numero > arquivo.size()) {
			throw new ChecagemException(getClass(), ERRO + " >>> op1 numero maior que arquivo");
		}
		String string = arquivo.get(numero.intValue() - 1);
		return new Linha(numero.intValue(), string);
	}

	static List<String> get(List<?> lista) {
		List<String> resp = new ArrayList<>();
		for (Object obj : lista) {
			resp.add(obj.toString());
		}
		return resp;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "linhaPeloNumero(List<String>, Numero) : Linha";
	}
}