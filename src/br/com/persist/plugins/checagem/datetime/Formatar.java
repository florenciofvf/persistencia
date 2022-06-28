package br.com.persist.plugins.checagem.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class Formatar extends FuncaoBinaria {
	private static final String ERRO = "Erro Formatar";
	private DateFormat format;

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(key, ctx);
		Object op1 = param1().executar(key, ctx);
		checkObrigatorioLong(op0, ERRO + " >>> op0");
		checkObrigatorioString(op1, ERRO + " >>> op1");
		Long pri = (Long) op0;
		String seg = (String) op1;
		if (format == null) {
			try {
				format = new SimpleDateFormat(seg);
			} catch (Exception e) {
				throw new ChecagemException("padrao invalido >>> " + seg);
			}
		}
		try {
			return format.format(new Date(pri));
		} catch (Exception e) {
			throw new ChecagemException("agora invalido >>> " + pri);
		}
	}
}