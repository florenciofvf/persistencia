package br.com.persist.plugins.checagem.atom;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinaria;

public class FormatarAgoraFuncao extends FuncaoBinaria {
	private static final String ERRO = "Erro formatar agora";
	private DateFormat format;

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		Object op1 = param1().executar(ctx);
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