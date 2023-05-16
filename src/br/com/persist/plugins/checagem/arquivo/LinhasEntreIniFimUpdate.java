package br.com.persist.plugins.checagem.arquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinariaOuMaior;

public class LinhasEntreIniFimUpdate extends FuncaoBinariaOuMaior implements Arquivo {
	private static final String ERRO = "Erro LinhasEntreIniFimUpdate";

	public LinhasEntreIniFimUpdate() {
		super(4);
	}

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checar(op0);
		Object op1 = param1().executar(checagem, bloco, ctx);
		Object op2 = parametros.get(2).executar(checagem, bloco, ctx);
		Object op3 = parametros.get(3).executar(checagem, bloco, ctx);
		checkObrigatorioString(op1, ERRO + " >>> op1");
		checkObrigatorioString(op2, ERRO + " >>> op2");
		checkObrigatorioString(op3, ERRO + " >>> op3");
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
			LinhasEntreIniFim.lancarExcecao(getClass(), ERRO, ctx, strInicio, strFinal);
		}
		String nova = (String) op3;
		List<Linha> resposta = new ArrayList<>();
		for (Linha linha : coletor) {
			String string = linha.string;
			int posIni = string.indexOf(strInicio);
			int posFim = string.indexOf(strFinal);
			String inicio = string.substring(0, posIni + strInicio.length());
			String termino = string.substring(posFim);
			resposta.add(new Linha(linha.numero, inicio + nova + termino));
		}
		return resposta;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "linhasEntreIniFimUpdate(List<String>, Texto, Texto, Texto) : List<Linha>";
	}
}