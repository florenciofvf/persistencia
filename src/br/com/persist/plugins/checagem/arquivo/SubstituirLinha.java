package br.com.persist.plugins.checagem.arquivo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.List;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinaria;

public class SubstituirLinha extends FuncaoBinaria {
	private static final String ERRO = "Erro SubstituirLinha";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		Object op1 = param1().executar(checagem, bloco, ctx);
		if (!(op1 instanceof Linha)) {
			throw new ChecagemException(getClass(), ERRO + " >>> op1 deve ser Linha");
		}
		try {
			String absoluto = (String) op0;
			Linha linha = (Linha) op1;
			if (linha.numero < 1) {
				throw new ChecagemException(getClass(), ERRO + " >>> op1 numero menor que 1");
			}
			List<String> arquivo = LerArquivo.lerArquivo(absoluto);
			if (linha.numero > arquivo.size()) {
				throw new ChecagemException(getClass(), ERRO + " >>> op1 numero maior que arquivo");
			}
			char c = ultimo(absoluto);
			PrintWriter pw = criarPrintWriter(absoluto);
			boolean sucesso = false;
			for (int i = 0, num = 1; i < arquivo.size(); i++, num++) {
				String string = arquivo.get(i);
				if (linha.processar(string, num, pw, num < arquivo.size())) {
					sucesso = true;
				}
			}
			if (c == '\r' || c == '\n') {
				pw.print(c);
			}
			pw.close();
			StringBuilder sb = new StringBuilder("SUBSTITUIR LINHA >>> " + absoluto);
			sb.append(sucesso ? " SUCESSO" : " NAO ALTERADO");
			return sb.toString();
		} catch (Exception ex) {
			throw new ChecagemException(getClass(), ERRO + " >>> " + ex.getMessage());
		}
	}

	private char ultimo(String absoluto) throws IOException {
		LerArquivo.checarArquivo(absoluto);
		try (RandomAccessFile raf = new RandomAccessFile(absoluto, "r")) {
			long length = raf.length();
			raf.seek(length - 1);
			return (char) raf.read();
		}
	}

	private PrintWriter criarPrintWriter(String absoluto) throws FileNotFoundException {
		return new PrintWriter(absoluto);
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "substituirLinha(Texto, Linha) : Texto";
	}
}