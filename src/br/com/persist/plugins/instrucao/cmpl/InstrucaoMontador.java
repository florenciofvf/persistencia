package br.com.persist.plugins.instrucao.cmpl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;

public class InstrucaoMontador {
	private InstrucaoMontador() {
	}

	public static boolean compilar(String arquivo) throws IOException, InstrucaoException {
		File file = new File(CacheBiblioteca.ROOT, arquivo);
		if (!file.isFile()) {
			return false;
		}
		String script = Util.conteudo(file);
		InstrucaoAtom lexico = new InstrucaoAtom(script);
		List<Atom> atoms = lexico.getListaAtom();
		InstrucaoGramatica gramatica = new InstrucaoGramatica(atoms);
		List<Metodo> metodos = gramatica.montarMetodos();
		File destino = new File(CacheBiblioteca.ROOT, arquivo + Biblioteca.EXTENSAO);
		try (PrintWriter pw = new PrintWriter(destino)) {
			for (Metodo metodo : metodos) {
				pw.println();
				metodo.print(pw);
			}
		}
		return true;
	}
}