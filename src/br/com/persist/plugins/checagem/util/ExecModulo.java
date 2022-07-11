package br.com.persist.plugins.checagem.util;

import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.ChecagemUtil;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;
import br.com.persist.plugins.checagem.Modulo;

public class ExecModulo extends FuncaoUnaria {
	private static final String ERRO = "Erro ExecModulo >>> ";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + "op0");
		String idModulo = (String) op0;
		ChecagemUtil.checarModulo(idModulo);
		Modulo modulo = checagem.getModulo(idModulo);
		if (modulo == null) {
			throwModuloInexistente(idModulo);
		}
		List<Object> lista = modulo.executar(checagem, null, ctx);
		StringBuilder sb = new StringBuilder();
		for (Object object : lista) {
			append(sb, object);
		}
		return sb.toString();
	}

	private void throwModuloInexistente(String idModulo) throws ChecagemException {
		throw new ChecagemException(getClass(), "Modulo inexistente! >>> " + idModulo);
	}

	private void append(StringBuilder sb, Object obj) {
		if (obj != null && !Util.estaVazio(obj.toString())) {
			if (sb.length() > 0) {
				sb.append(Constantes.QL);
			}
			sb.append(obj.toString());
		}
	}
}