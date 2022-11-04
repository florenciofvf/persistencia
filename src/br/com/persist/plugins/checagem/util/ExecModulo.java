package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.ChecagemUtil;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Modulo;
import br.com.persist.plugins.checagem.funcao.FuncaoBinaria;

public class ExecModulo extends FuncaoBinaria {
	private static final String ERRO = "Erro ExecModulo >>> ";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + "op0");
		Object op1 = parametros.get(1).executar(checagem, bloco, ctx);
		checkObrigatorioMap(op1, ERRO + "op1");
		String idModulo = (String) op0;
		ChecagemUtil.checarModulo(idModulo);
		Modulo modulo = checagem.getModulo(idModulo);
		if (modulo == null) {
			throwModuloInexistente(idModulo);
		}
		return modulo.executar(checagem, null, Contexto.criar(op1));
	}

	private void throwModuloInexistente(String idModulo) throws ChecagemException {
		throw new ChecagemException(getClass(), "Modulo inexistente! >>> " + idModulo);
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "execModulo(Texto, [Texto]) : Objeto";
	}
}